(ns com.wsscode.pathom.viz.trace
  (:require ["./d3-trace" :refer [renderPathomTrace updateTraceSize]]
            [fulcro.client.localized-dom :as dom]
            [fulcro.client.primitives :as fp]
            [goog.object :as gobj]
            [fulcro.client.mutations :as fm]))

(fp/defsc D3Trace [this _]
  {:css
   [[:.container {:flex 1}]

    [:$pathom-attribute
     {:fill    "#d4d4d4"
      :opacity "0.5"}

     [:&:hover
      {:fill "#94a0ad"}]]

    [:$pathom-attribute-bounds
     {:fill             "none"
      :opacity          "0.5"
      :stroke           "#000"
      :stroke-dasharray "5 1"
      :visibility       "hidden"}]

    [:$pathom-detail-marker
     {:fill    "#4ac380"
      :opacity "0.5"}

     [:&:hover
      {:opacity "0.7"}]]

    [:$pathom-event-waiting-resolver
     {:fill "#de5615"}]

    [:$pathom-event-skip-wait-key
     {:fill "#de5615"}]

    [:$pathom-event-external-wait-key
     {:fill "#de5615"}]

    [:$pathom-event-call-resolver
     {:fill "#7452fb"}]

    [:$pathom-event-call-resolver-batch
     {:fill "#2900cc"}]

    [:$pathom-event-schedule-resolver
     {:fill "#efaf42"}]

    [:$pathom-event-error
     {:fill "#bb0808"}]

    [:$pathom-label-text
     {:font-family    "sans-serif"
      :fill           "#222"
      :font-size      "11px"
      :pointer-events "none"}]

    [:$pathom-vruler
     {:stroke       "#2b98f0"
      :stroke-width "2px"
      :visibility   "hidden"}]

    [:$pathom-axis
     [:line
      {:stroke "#e5e5e5"}]]

    [:$pathom-tooltip
     {:position       "absolute"
      :pointer-events "none"
      :font-size      "12px"
      :font-family    "sans-serif"
      :background     "#fff"
      :padding        "1px 6px"
      :box-shadow     "#00000069 0px 1px 3px 0px"
      :white-space    "nowrap"}]]

   :componentDidMount
   (fn []
     (let [trace (-> this fp/props ::trace-data)
           container (gobj/get this "svgContainer")
           svg (gobj/get this "svg")]
       (gobj/set this "renderedData"
         (renderPathomTrace svg
           (clj->js {:svgWidth  (gobj/get container "clientWidth")
                     :svgHeight (gobj/get container "clientHeight")
                     :data      trace})))))

   :componentDidUpdate
   (fn [prev-props _]
     (let [container (gobj/get this "svgContainer")]
       (if (= (-> prev-props ::trace-data)
             (-> this fp/props ::trace-data))
         (updateTraceSize
           (doto (gobj/get this "renderedData")
             (gobj/set "svgWidth" (gobj/get container "clientWidth"))
             (gobj/set "svgHeight" (gobj/get container "clientHeight"))))
         (let [svg (gobj/get this "svg")]
           (gobj/set svg "innerHTML" "")
           (renderPathomTrace svg
             (clj->js {:svgWidth  (gobj/get container "clientWidth")
                       :svgHeight (gobj/get container "clientHeight")
                       :data      (-> this fp/props ::trace-data)}))))))}

  (dom/div :.container {:ref #(gobj/set this "svgContainer" %)}
    (dom/svg {:ref #(gobj/set this "svg" %)})))

(def d3-trace (fp/factory D3Trace))

(fp/defsc TraceView [this {:keys [expanded?]}]
  {:ident [:trace-id :trace-id]
   :query [:trace-id :trace-data :expanded?]
   :css   [[:.container {:width  "100%"
                         :height "300px"
                         :border "2px solid #da3939"}]
           [:.resized {:width  "80%"
                       :height "500px"}]]
   :componentDidMount
          (fn []
            (let [trace     (-> this fp/props :trace-data)
                  container (gobj/get this "svgContainer")
                  svg       (gobj/get this "svg")]
              (gobj/set this "renderedData"
                (renderPathomTrace svg
                  (clj->js {:svgWidth  (gobj/get container "clientWidth")
                            :svgHeight (gobj/get container "clientHeight")
                            :data      trace})))))

   :componentDidUpdate
          (fn [_ _]
            (let [container (gobj/get this "svgContainer")]
              (updateTraceSize
                (doto (gobj/get this "renderedData")
                  (gobj/set "svgWidth" (gobj/get container "clientWidth"))
                  (gobj/set "svgHeight" (gobj/get container "clientHeight"))))))}
  (dom/div
    (dom/button {:onClick #(fm/toggle! this :expanded?)} "Toggle Size")
    (dom/br)
    (dom/br)
    (dom/div :.container {:ref     #(gobj/set this "svgContainer" %)
                          :classes [(if expanded? :.resized)]}
      (dom/svg {:ref #(gobj/set this "svg" %)}))))

(def trace-view (fp/factory TraceView))
