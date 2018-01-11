(ns social-wallet-admin-console.vis.graph2d
  (:require [social-wallet-admin-console.dataset :refer :all]
            [cheshire.core :as json]
            [clojure.string :as str]
            [auxiliary.core :refer :all]
            [incanter.core :refer :all]
            [gorilla-renderable.core :as render])
  (:import (java.util UUID)))

(def content-template
  "<div id='==graph2d-id=='></div>
<script type='text/javascript'>
$(function () {
  var cachedScript = function(url, options) {
    // Allow user to set any option except for dataType, cache, and url
    options = $.extend( options || {}, {
      dataType: 'script',
      cache: true,
      url: url
    });
    // Use $.ajax() since it is more flexible than $.getScript
    // Return the jqXHR object so we can chain callbacks
    return jQuery.ajax(options);
  };
  var createGraph2d = function() {
    // create an array with xy dataset
    var xyseq = new vis.DataSet(==xyseq==);

    // create a network
    var container = document.getElementById('==graph2d-id==');

    // provide the data in the vis format
    var options = ==options==;

    // initialize your network!
    var network = new vis.Graph2d(container, xyseq, options);
  };
  if (!document.getElementById('vis-css')) {
    $('<link>')
      .attr('rel', 'stylesheet')
      .attr('href', 'https://cdnjs.cloudflare.com/ajax/libs/vis/4.20.0/vis.min.css')
      .attr('id', 'vis-css')
      .appendTo('head');
  }
  if (!window.visJsLoaded) {
    if (!window.visJsIsLoading) {
      window.visJsLoadedCallbacks = [createGraph2d];
      window.visJsIsLoading = true;
      cachedScript('https://cdnjs.cloudflare.com/ajax/libs/vis/4.20.0/vis.min.js')
        .done(function() {
          window.visJsIsLoading = false;
          window.visJsLoaded = true;
          _.each(window.visJsLoadedCallbacks, function(cb) { cb(); });
          window.visJsLoadedCallbacks = [];
        })
        .fail(function() { console.log('failed'); });
    } else {
      window.visJsLoadedCallbacks.push(createGraph2d);
    }
  } else {
    createGraph2d();
  }
});
</script>")

(defn- dataset->display-data
  "Converts an incanter dataset into an xy map for 2dgraphs."
  [data] {:pre [(dataset? data)]}
  (branch-on data

             transactions?
             (->> ($ [:timestamp :amount] data) :rows
                  (mapv #(hash-map :x (-> % first val) :y (-> % second val))))

             
             participants?
             (->> ($ [:x :y] data)
                  (mapv #(hash-map :x (first %) :y (second %))))))

(def default-options
  {:width  "600px"
   :height "400px"})

(defn render
  ([graph-data] (render graph-data {}))
  ([graph-data opts]
   (let [graph2d-id (str (UUID/randomUUID))
         ;; edges (edges->display-data (:edges graph-data))
         xyseq (dataset->display-data graph-data) ;; TODO: transformation from incanter dataset
         options (merge default-options
                        opts)]
     (-> content-template
         (str/replace #"==graph2d-id==" graph2d-id)
         (str/replace #"==xyseq==" (json/generate-string xyseq))
         (str/replace #"==options==" (json/generate-string options))))))

;; * Gorilla REPL rendering *
(defrecord GraphView [graph-data options])

(extend-type GraphView
  render/Renderable
  (render [{:keys [graph-data options] :as self}]
    {:type :html :content (render graph-data options) :value (hash self)}))

(defn graph2d
  ([graph-data] (graph2d graph-data {}))
  ([graph-data options]
   (GraphView. graph-data options)))

  
