(ns examples.opt
  (:require [clojure.core.matrix :as mat]
            [bopp.core :refer :all]
            [taoensso.tufte :as tufte :refer (defnp p profiled profile)])
  (:use [anglican runtime emit]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Classic optimization tests



(defdist myfactor
  []
  (sample* [this] 0)
  (observe* [this value] value))

(def pi Math/PI)

;;; Branin ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(with-primitive-procedures [myfactor]
 (defopt branin-opt [] [x1 x2]
   (let [l-x1 (- 5)
         u-x1 10
         l-x2 0
         u-x2 15
         x1 (sample (uniform-continuous l-x1 u-x1))
         x2 (sample (uniform-continuous l-x2 u-x2))
         t1 (- (+ (- x2 (* (pow x1 2) (/ 5.1 (* 4 (pow pi 2)))))
                  (* x1 (/ 5 pi)))
               6)
         t2 (* 10
               (- 1 (/ 1 (* 8 pi)))
               (cos x1))
         branin (- 0 (+ (pow t1 2) t2 10))
         prior-correction (+ (log (- u-x1 l-x1)) (log (- u-x2 l-x2)))]
     (observe (myfactor) (+ branin prior-correction)))))


(defn branin-test [n-steps common-name n-test]
  (def branin-seq
    (doopt :importance
           branin-opt
           []
           1
           :bo-verbose true
           :bo-debug-folder (str "branin" common-name n-test)))
  (mapv #(first %) (doall (take n-steps branin-seq)))) ; n-steps = 200

;;; LDA ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def lda-output-grid
  [[[5.258112826000000   5.149513805000000   5.229181160000000   5.022511473000000   5.000332115999999   4.432511510000000]
    [3.624167804000000   3.481699285000000   3.346584734000000   3.198956320000000   2.939348266000000   2.542238814000000]
    [2.836012122000000   2.804748620000000   2.612878741000000   2.346008412000000   2.088040327000000   1.880399470000000]
    [2.227201820000000   2.084897678000000   1.969256740000000   1.786323249000000   1.626654117000000   1.520064450000000]
    [1.730222585000000   1.643681520000000   1.549807417000000   1.457530339000000   1.401072001000000   1.364836274000000]
    [1.460865658000000   1.413822886000000   1.364872251000000   1.329959506000000   1.307561280000000   1.302023694000000]
    [1.329484556000000   1.309276414000000   1.292698578000000   1.278813005000000   1.274208686000000   1.296897775000000]
    [1.271753365000000   1.270589627000000   1.266167382000000   1.271869567000000   1.278123799000000   1.321591240000000]]
   [[3.722019016000000   3.747871323000000   3.770110326000000   3.619232213000000   3.294021457000000   2.918561304000000]
    [3.126891155000000   3.053489799000000   2.859440420000000   2.630424468000000   2.360980123000000   2.045705842000000]
    [2.601345619000000   2.482891469000000   2.277597145000000   2.033429989000000   1.800429160000000   1.626743743000000]
    [2.086676125000000   1.943349721000000   1.786518680000000   1.630248431000000   1.501526137000000   1.438727854000000]
    [1.660049798000000   1.568538127000000   1.473258879000000   1.395590972000000   1.353926401000000   1.351998671000000]
    [1.435221761000000   1.382858640000000   1.334947039000000   1.309290893000000   1.302341384000000   1.317814555000000]
    [1.317818664000000   1.299255183000000   1.285295193000000   1.271714717000000   1.290394505000000   1.328191297000000]
    [1.272839987000000   1.270425851000000   1.267214011000000   1.268070557000000   1.310790773000000   1.393614637000000]]
   [[2.975404068000000   2.968382913000000   2.961509740000000   2.810881943000000   2.566918453000000   2.204569258000000]
    [2.729267232000000   2.711405961000000   2.487987431000000   2.250809494000000   1.951044824000000   1.711022236000000]
    [2.422500695000000   2.259310536000000   2.035164063000000   1.784594764000000   1.594416105000000   1.505384479000000]
    [1.983587762000000   1.843379712000000   1.668761813000000   1.505616635000000   1.427856381000000   1.413755921000000]
    [1.619226174000000   1.521322055000000   1.419439444000000   1.359437760000000   1.343241802000000   1.363001016000000]
    [1.414752908000000   1.360104102000000   1.318230944000000   1.303411361000000   1.309209494000000   1.369038389000000]
    [1.313263743000000   1.291760503000000   1.281538285000000   1.276161920000000   1.314937940000000   1.397052266000000]
    [1.276738560000000   1.273726730000000   1.271574590000000   1.282492457000000   1.377931876000000   1.524871729000000]]
   [[2.548705258000000   2.566382537000000   2.560527040000000   2.347846308000000   2.077487352000000   1.787887829000000]
    [2.477453345000000   2.384413805000000   2.175249552000000   1.943102596000000   1.713158518000000   1.528555961000000]
    [2.271844459000000   2.101979941000000   1.836733098000000   1.631072354000000   1.504079040000000   1.509744747000000]
    [1.917207978000000   1.752565517000000   1.572391232000000   1.454028740000000   1.408231244000000   1.447394204000000]
    [1.590643012000000   1.478045732000000   1.385504627000000   1.349812010000000   1.347624790000000   1.389175687000000]
    [1.406231488000000   1.343234780000000   1.309582372000000   1.304882655000000   1.330546637000000   1.420742784000000]
    [1.310060860000000   1.289574825000000   1.280470261000000   1.296580970000000   1.361320422000000   1.537873629000000]
    [1.281177353000000   1.273680399000000   1.277089752000000   1.320508949000000   1.442360685000000   1.670162786000000]]
   [[2.364737107000000   2.452668607000000   2.341462081000000   2.118723891000000   1.865777546000000   1.654893494000000]
    [2.429684277000000   2.240094436000000   2.040531678000000   1.789856352000000   1.601761693000000   1.513696030000000]
    [2.210134257000000   2.006496131000000   1.749505296000000   1.541968527000000   1.509932842000000   1.586647560000000]
    [1.886883404000000   1.706756992000000   1.537363365000000   1.454543818000000   1.453060437000000   1.506415675000000]
    [1.582721013000000   1.463333299000000   1.381783824000000   1.358717854000000   1.391712439000000   1.468342464000000]
    [1.405254246000000   1.345175995000000   1.317263697000000   1.318388432000000   1.392699949000000   1.521803360000000]
    [1.314611328000000   1.294339616000000   1.284176859000000   1.318135676000000   1.439558928000000   1.680540179000000]
    [1.287551166000000   1.280796294000000   1.282164335000000   1.383792566000000   1.568119023000000   1.929981674000000]]
   [[2.547784743000000   2.562137775000000   2.369474345000000   2.109894998000000   1.855132826000000   1.693890539000000]
    [2.524250165000000   2.306935465000000   2.079802409000000   1.812145883000000   1.620128845000000   1.637661866000000]
    [2.244860585000000   2.014255351000000   1.750590736000000   1.572944521000000   1.593476294000000   1.696944579000000]
    [1.913104369000000   1.718817531000000   1.558076377000000   1.495508178000000   1.503488973000000   1.579061710000000]
    [1.593512913000000   1.472690781000000   1.404940141000000   1.388593292000000   1.417792032000000   1.612244480000000]
    [1.416648457000000   1.358437431000000   1.335264417000000   1.343801912000000   1.450749101000000   1.738719119000000]
    [1.323445870000000   1.304324411000000   1.295767130000000   1.356962017000000   1.588528832000000   1.847090224000000]
    [1.301175618000000   1.293198126000000   1.302230813000000   1.433882412000000   1.697459511000000   2.250711024000000]]])

(defm lda-eval [s tau kappa]
  (- 0 (nth
        (nth
         (nth lda-output-grid
              (int kappa))
         (int s))
        (int tau))))

(with-primitive-procedures [myfactor]
 (defopt lda-opt [] [s tau kappa]
   (let [s (sample (uniform-discrete 0 8))
         tau (sample (uniform-discrete 0 6))
         kappa (sample (uniform-discrete 0 6))
         lda-val (lda-eval s tau kappa)
         prior-correction (log 288)]
     (observe (myfactor) (+ lda-val prior-correction)))))

(defn lda-test [n-steps common-name n-test]
  (def lda-seq
    (doopt :importance
           lda-opt
           []
           2
           :bo-verbose true
           :bo-debug-folder (str "LDA" common-name n-test)))
  (mapv #(first %) (doall (take n-steps lda-seq)))) ; n-steps = 50

;;; SVM ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def svm-output-grid
  [[[0.473300000000000	0.466000000000000	0.457200000000000	0.447420000000000	0.437600000000000	0.427140000000000	0.416880000000000	0.408280000000000	0.399220000000000	0.391420000000000	0.357200000000000	0.333340000000000	0.310420000000000	0.291000000000000]
    [0.473180000000000	0.463680000000000	0.454620000000000	0.444140000000000	0.433880000000000	0.422060000000000	0.411520000000000	0.401540000000000	0.392680000000000	0.386740000000000	0.347820000000000	0.327260000000000	0.304500000000000	0.287140000000000]
    [0.460140000000000	0.450900000000000	0.441320000000000	0.430320000000000	0.421060000000000	0.412240000000000	0.402320000000000	0.393840000000000	0.386180000000000	0.384360000000000	0.346280000000000	0.325160000000000	0.304880000000000	0.288680000000000]
    [0.448920000000000	0.439620000000000	0.429760000000000	0.419560000000000	0.411320000000000	0.402120000000000	0.394700000000000	0.385920000000000	0.377900000000000	0.382880000000000	0.338820000000000	0.320000000000000	0.302140000000000	0.287220000000000]
    [0.428540000000000	0.416360000000000	0.404700000000000	0.393880000000000	0.384200000000000	0.375960000000000	0.368020000000000	0.361120000000000	0.354340000000000	0.365620000000000	0.355480000000000	0.344100000000000	0.331080000000000	0.324260000000000]
    [0.399820000000000	0.386220000000000	0.375100000000000	0.364980000000000	0.356000000000000	0.349200000000000	0.343020000000000	0.337680000000000	0.333820000000000	0.347100000000000	0.310740000000000	0.293780000000000	0.282180000000000	0.275660000000000]
    [0.372760000000000	0.360200000000000	0.350820000000000	0.343200000000000	0.337020000000000	0.332200000000000	0.327460000000000	0.323260000000000	0.319500000000000	0.333920000000000	0.308420000000000	0.292300000000000	0.280320000000000	0.276000000000000]
    [0.348880000000000	0.339040000000000	0.332740000000000	0.327760000000000	0.323200000000000	0.318920000000000	0.315960000000000	0.313300000000000	0.311060000000000	0.323840000000000	0.300120000000000	0.285500000000000	0.276620000000000	0.274020000000000]
    [0.299660000000000	0.297640000000000	0.297180000000000	0.297060000000000	0.296760000000000	0.296020000000000	0.295540000000000	0.294900000000000	0.294280000000000	0.299420000000000	0.292400000000000	0.285000000000000	0.276420000000000	0.271760000000000]
    [0.285940000000000	0.286580000000000	0.286540000000000	0.286800000000000	0.286780000000000	0.286700000000000	0.286460000000000	0.286640000000000	0.287260000000000	0.289280000000000	0.287380000000000	0.274040000000000	0.267620000000000	0.268540000000000]
    [0.278720000000000	0.279420000000000	0.279940000000000	0.280920000000000	0.281460000000000	0.282240000000000	0.282940000000000	0.282880000000000	0.283520000000000	0.284980000000000	0.284840000000000	0.266580000000000	0.266440000000000	0.267740000000000]
    [0.274020000000000	0.275300000000000	0.276540000000000	0.277660000000000	0.278400000000000	0.279260000000000	0.280060000000000	0.280660000000000	0.281420000000000	0.282400000000000	0.270620000000000	0.265140000000000	0.266200000000000	0.267440000000000]
    [0.271160000000000	0.272900000000000	0.274200000000000	0.275460000000000	0.276120000000000	0.276800000000000	0.277680000000000	0.278820000000000	0.279580000000000	0.279940000000000	0.263480000000000	0.264860000000000	0.265940000000000	0.267200000000000]
    [0.269225000000000	0.270950000000000	0.272225000000000	0.273425000000000	0.274175000000000	0.275225000000000	0.276125000000000	0.277150000000000	0.277725000000000	0.278375000000000	0.264075000000000	0.264600000000000	0.265800000000000	0.267875000000000]
    [0.268380000000000	0.270100000000000	0.271500000000000	0.272380000000000	0.273500000000000	0.274980000000000	0.275960000000000	0.276680000000000	0.268900000000000	0.277680000000000	0.262360000000000	0.264020000000000	0.265320000000000	0.267420000000000]
    [0.267533333333000	0.269333333333000	0.270200000000000	0.271500000000000	0.272566666667000	0.273600000000000	0.274433333333000	0.261333333333000	0.255766666667000	0.276400000000000	0.259700000000000	0.261500000000000	0.264100000000000	0.268100000000000]
    [0.266766666667000	0.268366666667000	0.269400000000000	0.271066666667000	0.272200000000000	0.265433333333000	0.259833333333000	0.254400000000000	0.254866666667000	0.276066666667000	0.258300000000000	0.260866666667000	0.263733333333000	0.268700000000000]
    [0.244475000000000	0.245550000000000	0.247325000000000	0.247850000000000	0.249000000000000	0.249800000000000	0.251025000000000	0.251625000000000	0.252625000000000	0.262250000000000	0.257950000000000	0.260950000000000	0.264600000000000	0.270400000000000]
    [0.242260000000000	0.243920000000000	0.245840000000000	0.247560000000000	0.248360000000000	0.249240000000000	0.250820000000000	0.251680000000000	0.252420000000000	0.256840000000000	0.258380000000000	0.260960000000000	0.264840000000000	0.269500000000000]
    [0.241680000000000	0.243960000000000	0.245420000000000	0.246620000000000	0.247960000000000	0.249140000000000	0.250160000000000	0.251380000000000	0.253320000000000	0.255980000000000	0.258000000000000	0.260760000000000	0.265300000000000	0.269020000000000]
    [0.241960000000000	0.243800000000000	0.245400000000000	0.246660000000000	0.247960000000000	0.248860000000000	0.251880000000000	0.251160000000000	0.258060000000000	0.257700000000000	0.266040000000000	0.260660000000000	0.265620000000000	0.268120000000000]
    [0.241880000000000	0.243700000000000	0.245460000000000	0.246520000000000	0.247680000000000	0.252060000000000	0.250760000000000	0.255080000000000	0.261060000000000	0.263360000000000	0.263380000000000	0.265660000000000	0.265620000000000	0.268360000000000]
    [0.241740000000000	0.243580000000000	0.245340000000000	0.246640000000000	0.247360000000000	0.248840000000000	0.260380000000000	0.252640000000000	0.256800000000000	0.262720000000000	0.268040000000000	0.264880000000000	0.267760000000000	0.268440000000000]
    [0.241780000000000	0.243120000000000	0.245300000000000	0.246100000000000	0.247100000000000	0.254000000000000	0.273420000000000	0.270320000000000	0.289780000000000	0.268840000000000	0.282500000000000	0.280560000000000	0.270120000000000	0.271620000000000]
    [0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.490760000000000	0.426020000000000	0.411120000000000	0.401180000000000]]
[[0.468420000000000	0.459720000000000	0.450760000000000	0.440280000000000	0.431420000000000	0.421300000000000	0.413400000000000	0.403960000000000	0.394860000000000	0.387320000000000	0.355700000000000	0.336480000000000	0.313160000000000	0.295420000000000]
[0.469260000000000	0.460200000000000	0.452300000000000	0.443340000000000	0.433220000000000	0.422500000000000	0.413940000000000	0.403620000000000	0.394800000000000	0.389120000000000	0.354340000000000	0.334260000000000	0.311720000000000	0.294140000000000]
[0.454500000000000	0.444940000000000	0.434900000000000	0.425400000000000	0.416480000000000	0.407560000000000	0.398640000000000	0.391640000000000	0.383720000000000	0.381300000000000	0.347660000000000	0.331100000000000	0.348640000000000	0.334660000000000]
[0.446440000000000	0.437180000000000	0.426360000000000	0.415180000000000	0.405440000000000	0.396800000000000	0.389700000000000	0.381660000000000	0.375180000000000	0.376680000000000	0.341780000000000	0.326520000000000	0.310040000000000	0.295680000000000]
[0.428240000000000	0.416880000000000	0.404720000000000	0.394240000000000	0.385180000000000	0.376140000000000	0.368620000000000	0.362260000000000	0.354800000000000	0.368320000000000	0.397740000000000	0.390020000000000	0.382140000000000	0.375260000000000]
[0.402300000000000	0.388300000000000	0.377420000000000	0.366760000000000	0.358520000000000	0.351300000000000	0.345260000000000	0.339760000000000	0.335260000000000	0.349260000000000	0.317380000000000	0.308700000000000	0.292600000000000	0.281220000000000]
[0.374220000000000	0.362060000000000	0.352080000000000	0.344580000000000	0.338160000000000	0.333220000000000	0.328580000000000	0.324520000000000	0.321480000000000	0.334340000000000	0.347120000000000	0.341880000000000	0.327220000000000	0.321940000000000]
[0.349460000000000	0.340580000000000	0.333300000000000	0.328840000000000	0.324060000000000	0.320340000000000	0.317460000000000	0.314600000000000	0.312140000000000	0.324060000000000	0.304380000000000	0.297940000000000	0.286340000000000	0.279540000000000]
[0.300180000000000	0.298100000000000	0.297620000000000	0.297120000000000	0.296780000000000	0.296420000000000	0.296060000000000	0.295380000000000	0.295160000000000	0.300500000000000	0.292840000000000	0.291220000000000	0.289440000000000	0.287240000000000]
[0.286040000000000	0.286860000000000	0.286620000000000	0.287000000000000	0.287120000000000	0.287340000000000	0.287440000000000	0.287420000000000	0.287620000000000	0.289800000000000	0.288220000000000	0.288000000000000	0.286960000000000	0.276460000000000]
[0.279080000000000	0.279900000000000	0.280380000000000	0.281160000000000	0.281940000000000	0.282660000000000	0.282800000000000	0.283440000000000	0.283880000000000	0.285200000000000	0.285300000000000	0.285620000000000	0.281740000000000	0.271580000000000]
[0.274440000000000	0.275680000000000	0.276940000000000	0.277820000000000	0.278480000000000	0.279300000000000	0.279760000000000	0.280760000000000	0.281800000000000	0.282100000000000	0.283660000000000	0.284800000000000	0.277100000000000	0.271020000000000]
[0.271140000000000	0.272920000000000	0.274460000000000	0.275420000000000	0.276300000000000	0.277500000000000	0.278440000000000	0.279460000000000	0.280380000000000	0.280620000000000	0.282500000000000	0.283340000000000	0.276460000000000	0.267520000000000]
[0.269600000000000	0.271380000000000	0.272860000000000	0.274100000000000	0.275220000000000	0.276060000000000	0.277040000000000	0.278440000000000	0.279340000000000	0.279460000000000	0.281640000000000	0.282640000000000	0.268600000000000	0.270860000000000]
[0.268760000000000	0.270180000000000	0.271760000000000	0.272740000000000	0.273820000000000	0.275280000000000	0.276480000000000	0.277300000000000	0.278100000000000	0.278500000000000	0.281000000000000	0.277980000000000	0.268840000000000	0.267940000000000]
[0.267760000000000	0.269620000000000	0.270840000000000	0.271680000000000	0.273280000000000	0.274660000000000	0.275740000000000	0.276680000000000	0.277120000000000	0.277400000000000	0.280280000000000	0.277220000000000	0.268520000000000	0.270160000000000]
[0.267020000000000	0.268740000000000	0.269940000000000	0.271020000000000	0.272660000000000	0.273980000000000	0.275200000000000	0.276120000000000	0.276820000000000	0.276960000000000	0.279840000000000	0.276800000000000	0.265100000000000	0.267840000000000]
[0.262840000000000	0.264580000000000	0.265860000000000	0.267400000000000	0.268520000000000	0.269940000000000	0.266900000000000	0.260980000000000	0.257820000000000	0.272440000000000	0.258740000000000	0.260800000000000	0.264760000000000	0.268220000000000]
[0.252940000000000	0.254980000000000	0.249940000000000	0.247100000000000	0.248680000000000	0.249680000000000	0.250620000000000	0.252040000000000	0.252840000000000	0.269620000000000	0.257580000000000	0.260520000000000	0.264360000000000	0.268080000000000]
[0.241780000000000	0.243680000000000	0.245200000000000	0.246680000000000	0.247940000000000	0.249500000000000	0.250780000000000	0.251360000000000	0.252300000000000	0.262180000000000	0.257700000000000	0.260200000000000	0.265240000000000	0.269380000000000]
[0.241660000000000	0.243240000000000	0.245120000000000	0.246620000000000	0.248200000000000	0.249360000000000	0.249860000000000	0.251000000000000	0.251640000000000	0.260340000000000	0.257240000000000	0.260140000000000	0.264740000000000	0.269200000000000]
[0.241100000000000	0.243700000000000	0.245020000000000	0.246300000000000	0.248240000000000	0.249240000000000	0.249720000000000	0.250660000000000	0.251700000000000	0.266640000000000	0.257000000000000	0.260320000000000	0.264760000000000	0.267900000000000]
[0.241460000000000	0.243140000000000	0.244940000000000	0.246400000000000	0.247860000000000	0.249360000000000	0.249880000000000	0.250260000000000	0.251460000000000	0.266520000000000	0.257460000000000	0.260080000000000	0.264660000000000	0.268760000000000]
[0.241360000000000	0.243300000000000	0.245120000000000	0.246260000000000	0.247460000000000	0.248620000000000	0.249660000000000	0.250380000000000	0.251400000000000	0.268320000000000	0.257140000000000	0.259880000000000	0.265520000000000	0.271240000000000]
[0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.487880000000000	0.431440000000000	0.391300000000000	0.407360000000000]]
   [[0.469460000000000	0.462060000000000	0.453060000000000	0.446100000000000	0.433200000000000	0.429140000000000	0.418020000000000	0.410620000000000	0.405860000000000	0.389220000000000	0.356200000000000	0.344300000000000	0.317220000000000	0.300220000000000]
[0.466780000000000	0.458940000000000	0.449940000000000	0.445320000000000	0.434720000000000	0.428180000000000	0.413880000000000	0.408900000000000	0.398340000000000	0.399120000000000	0.360920000000000	0.343180000000000	0.316040000000000	0.297580000000000]
[0.460820000000000	0.452660000000000	0.447100000000000	0.432060000000000	0.425500000000000	0.414700000000000	0.409320000000000	0.396820000000000	0.388360000000000	0.392720000000000	0.353620000000000	0.336140000000000	0.314160000000000	0.298640000000000]
[0.453700000000000	0.444620000000000	0.436700000000000	0.425980000000000	0.414060000000000	0.404300000000000	0.392900000000000	0.386500000000000	0.379860000000000	0.388400000000000	0.346700000000000	0.330960000000000	0.309060000000000	0.293900000000000]
[0.441880000000000	0.430520000000000	0.415600000000000	0.407680000000000	0.394140000000000	0.390180000000000	0.375300000000000	0.373380000000000	0.365980000000000	0.372020000000000	0.335840000000000	0.320520000000000	0.304880000000000	0.292640000000000]
[0.409800000000000	0.397060000000000	0.382280000000000	0.372580000000000	0.363300000000000	0.359860000000000	0.352940000000000	0.344180000000000	0.340620000000000	0.353540000000000	0.320120000000000	0.312180000000000	0.300700000000000	0.291940000000000]
[0.380560000000000	0.370600000000000	0.359300000000000	0.348280000000000	0.341640000000000	0.335740000000000	0.332560000000000	0.328760000000000	0.326160000000000	0.335820000000000	0.312200000000000	0.306360000000000	0.298000000000000	0.289440000000000]
[0.354480000000000	0.345620000000000	0.338000000000000	0.332080000000000	0.328160000000000	0.322820000000000	0.319900000000000	0.317060000000000	0.313600000000000	0.325080000000000	0.304220000000000	0.298680000000000	0.292760000000000	0.287240000000000]
[0.301840000000000	0.299120000000000	0.297880000000000	0.297280000000000	0.296800000000000	0.296560000000000	0.296220000000000	0.295480000000000	0.294420000000000	0.300640000000000	0.293020000000000	0.291120000000000	0.289800000000000	0.287220000000000]
[0.286040000000000	0.286460000000000	0.286700000000000	0.287000000000000	0.287500000000000	0.287200000000000	0.287420000000000	0.287160000000000	0.287980000000000	0.289900000000000	0.287180000000000	0.287920000000000	0.287160000000000	0.285780000000000]
[0.278540000000000	0.279760000000000	0.280540000000000	0.281800000000000	0.282320000000000	0.282140000000000	0.282580000000000	0.283360000000000	0.283675000000000	0.284400000000000	0.284500000000000	0.285140000000000	0.285820000000000	0.285140000000000]
[0.274200000000000	0.275880000000000	0.276720000000000	0.277900000000000	0.278280000000000	0.279200000000000	0.280040000000000	0.281120000000000	0.281060000000000	0.280420000000000	0.283320000000000	0.284060000000000	0.284100000000000	0.285400000000000]
[0.271720000000000	0.272660000000000	0.274500000000000	0.275180000000000	0.276200000000000	0.277060000000000	0.278100000000000	0.279120000000000	0.280340000000000	0.279460000000000	0.282580000000000	0.283000000000000	0.284240000000000	0.284780000000000]
[0.269680000000000	0.271040000000000	0.272900000000000	0.273740000000000	0.275020000000000	0.275780000000000	0.276780000000000	0.278080000000000	0.278720000000000	0.277780000000000	0.281300000000000	0.282500000000000	0.284440000000000	0.284800000000000]
[0.268320000000000	0.270040000000000	0.271240000000000	0.272680000000000	0.273760000000000	0.274660000000000	0.275860000000000	0.276700000000000	0.277940000000000	0.276100000000000	0.280660000000000	0.282620000000000	0.283720000000000	0.283800000000000]
[0.267340000000000	0.269160000000000	0.270140000000000	0.271820000000000	0.273120000000000	0.274540000000000	0.275060000000000	0.276060000000000	0.277080000000000	0.276000000000000	0.280380000000000	0.281820000000000	0.282900000000000	0.284440000000000]
[0.266480000000000	0.268500000000000	0.269540000000000	0.270980000000000	0.272420000000000	0.273460000000000	0.275080000000000	0.275780000000000	0.277300000000000	0.275560000000000	0.279840000000000	0.281040000000000	0.282500000000000	0.283280000000000]
[0.262900000000000	0.264220000000000	0.266060000000000	0.267480000000000	0.269675000000000	0.270580000000000	0.271740000000000	0.272480000000000	0.273620000000000	0.273080000000000	0.276680000000000	0.277660000000000	0.280840000000000	0.281225000000000]
[0.260700000000000	0.262380000000000	0.264540000000000	0.265620000000000	0.267240000000000	0.268380000000000	0.269220000000000	0.270140000000000	0.270260000000000	0.271260000000000	0.273420000000000	0.274580000000000	0.277340000000000	0.279220000000000]
[0.259180000000000	0.261560000000000	0.262840000000000	0.264180000000000	0.265180000000000	0.265820000000000	0.266300000000000	0.266720000000000	0.266920000000000	0.268600000000000	0.270960000000000	0.272800000000000	0.276900000000000	0.278480000000000]
[0.258440000000000	0.260040000000000	0.260760000000000	0.262060000000000	0.259200000000000	0.262460000000000	0.263300000000000	0.263280000000000	0.265400000000000	0.266980000000000	0.268380000000000	0.271080000000000	0.274860000000000	0.276620000000000]
[0.257080000000000	0.256660000000000	0.254780000000000	0.255220000000000	0.253440000000000	0.254780000000000	0.260300000000000	0.257920000000000	0.262300000000000	0.267900000000000	0.266300000000000	0.264920000000000	0.272820000000000	0.276900000000000]
[0.250520000000000	0.244500000000000	0.245200000000000	0.246300000000000	0.248340000000000	0.249580000000000	0.249860000000000	0.251480000000000	0.256540000000000	0.270200000000000	0.257720000000000	0.263180000000000	0.268000000000000	0.277040000000000]
[0.242060000000000	0.242580000000000	0.244760000000000	0.246660000000000	0.247260000000000	0.248620000000000	0.249620000000000	0.255500000000000	0.257040000000000	0.268660000000000	0.264620000000000	0.263880000000000	0.268280000000000	0.275560000000000]
[0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.497620000000000	0.408060000000000	0.374300000000000	0.407820000000000]]
   [[0.493140000000000	0.484580000000000	0.475660000000000	0.471300000000000	0.474300000000000	0.456860000000000	0.454120000000000	0.446020000000000	0.447020000000000	0.442620000000000	0.398050000000000	0.398580000000000	0.357620000000000	0.344100000000000]
[0.484620000000000	0.476760000000000	0.470440000000000	0.458240000000000	0.467400000000000	0.451420000000000	0.437420000000000	0.431200000000000	0.424660000000000	0.420180000000000	0.392980000000000	0.367740000000000	0.345160000000000	0.327040000000000]
[0.487600000000000	0.472100000000000	0.462820000000000	0.467440000000000	0.457800000000000	0.439580000000000	0.434300000000000	0.438540000000000	0.406140000000000	0.433080000000000	0.428060000000000	0.386260000000000	0.359280000000000	0.333360000000000]
[0.494400000000000	0.482260000000000	0.469980000000000	0.478180000000000	0.481280000000000	0.478920000000000	0.478220000000000	0.480620000000000	0.468260000000000	0.450740000000000	0.414160000000000	0.455160000000000	0.400060000000000	0.378700000000000]
[0.471860000000000	0.469380000000000	0.465600000000000	0.450900000000000	0.445980000000000	0.436020000000000	0.433460000000000	0.383420000000000	0.416700000000000	0.416520000000000	0.377260000000000	0.326460000000000	0.335000000000000	0.331840000000000]
[0.470240000000000	0.465360000000000	0.460480000000000	0.447140000000000	0.453020000000000	0.443740000000000	0.431140000000000	0.419620000000000	0.394440000000000	0.421880000000000	0.381360000000000	0.364540000000000	0.341920000000000	0.338860000000000]
[0.429980000000000	0.426460000000000	0.403700000000000	0.395240000000000	0.387420000000000	0.379460000000000	0.364320000000000	0.368580000000000	0.347760000000000	0.370680000000000	0.328120000000000	0.310560000000000	0.298080000000000	0.300280000000000]
[0.397200000000000	0.388740000000000	0.379125000000000	0.360140000000000	0.356760000000000	0.338200000000000	0.331740000000000	0.324480000000000	0.319140000000000	0.325680000000000	0.305460000000000	0.303960000000000	0.295620000000000	0.289920000000000]
[0.299800000000000	0.300500000000000	0.301200000000000	0.294100000000000	0.296525000000000	0.293560000000000	0.294920000000000	0.295900000000000	0.295260000000000	0.293640000000000	0.292060000000000	0.289260000000000	0.287140000000000	0.287160000000000]
[0.283040000000000	0.285240000000000	0.285675000000000	0.283920000000000	0.287220000000000	0.287280000000000	0.285760000000000	0.286720000000000	0.287700000000000	0.294120000000000	0.287480000000000	0.284200000000000	0.287360000000000	0.286520000000000]
[0.277275000000000	0.277940000000000	0.278860000000000	0.280060000000000	0.281740000000000	0.281980000000000	0.281540000000000	0.281640000000000	0.284675000000000	0.286680000000000	0.284740000000000	0.282160000000000	0.285940000000000	0.285140000000000]
[0.271720000000000	0.273620000000000	0.276620000000000	0.276720000000000	0.277560000000000	0.277225000000000	0.277520000000000	0.278900000000000	0.281260000000000	0.283660000000000	0.284820000000000	0.281960000000000	0.282180000000000	0.282220000000000]
[0.269420000000000	0.269560000000000	0.273020000000000	0.274500000000000	0.274020000000000	0.275440000000000	0.276140000000000	0.279260000000000	0.278820000000000	0.283780000000000	0.281520000000000	0.282320000000000	0.281340000000000	0.282940000000000]
[0.266900000000000	0.268720000000000	0.270880000000000	0.272800000000000	0.273100000000000	0.272840000000000	0.274080000000000	0.276860000000000	0.277220000000000	0.293580000000000	0.280950000000000	0.281600000000000	0.281460000000000	0.282820000000000]
[0.265220000000000	0.268380000000000	0.270960000000000	0.270280000000000	0.273460000000000	0.273860000000000	0.274520000000000	0.275280000000000	0.275020000000000	0.288100000000000	0.279580000000000	0.280300000000000	0.281100000000000	0.281920000000000]
[0.265000000000000	0.266080000000000	0.268240000000000	0.270520000000000	0.271020000000000	0.273340000000000	0.273220000000000	0.274200000000000	0.273280000000000	0.286900000000000	0.277940000000000	0.280260000000000	0.281260000000000	0.282140000000000]
[0.264900000000000	0.265900000000000	0.268620000000000	0.271500000000000	0.271500000000000	0.271800000000000	0.273240000000000	0.273700000000000	0.273700000000000	0.279340000000000	0.278260000000000	0.278800000000000	0.281000000000000	0.281120000000000]
[0.261800000000000	0.263260000000000	0.265400000000000	0.266640000000000	0.268260000000000	0.268720000000000	0.269080000000000	0.269960000000000	0.271180000000000	0.273300000000000	0.273260000000000	0.274760000000000	0.277380000000000	0.279100000000000]
[0.260740000000000	0.261840000000000	0.263740000000000	0.263600000000000	0.264760000000000	0.268100000000000	0.267900000000000	0.268500000000000	0.267780000000000	0.272020000000000	0.271660000000000	0.274480000000000	0.274840000000000	0.279020000000000]
[0.257420000000000	0.257860000000000	0.258020000000000	0.260120000000000	0.259900000000000	0.260520000000000	0.262300000000000	0.261380000000000	0.263940000000000	0.270020000000000	0.268660000000000	0.268760000000000	0.273020000000000	0.276220000000000]
[0.258740000000000	0.259340000000000	0.260900000000000	0.260540000000000	0.261540000000000	0.263660000000000	0.263520000000000	0.263500000000000	0.265580000000000	0.272360000000000	0.268000000000000	0.274280000000000	0.275100000000000	0.279540000000000]
[0.254680000000000	0.254400000000000	0.255780000000000	0.257640000000000	0.258660000000000	0.259340000000000	0.260840000000000	0.264380000000000	0.262480000000000	0.268100000000000	0.266840000000000	0.270340000000000	0.274140000000000	0.278940000000000]
[0.255000000000000	0.255260000000000	0.256800000000000	0.257280000000000	0.258700000000000	0.262140000000000	0.260800000000000	0.263440000000000	0.265000000000000	0.268660000000000	0.268660000000000	0.271600000000000	0.274980000000000	0.279980000000000]
[0.252900000000000	0.254300000000000	0.257260000000000	0.258320000000000	0.260960000000000	0.262440000000000	0.265020000000000	0.263540000000000	0.269620000000000	0.280320000000000	0.274060000000000	0.276740000000000	0.273040000000000	0.280420000000000]
[0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.500000000000000	0.478120000000000	0.472740000000000	0.452060000000000	0.284200000000000]]])

(defm svm-eval [c alpha epsilon]
  (- 0 (nth
        (nth
         (nth svm-output-grid
              (int epsilon))
         (int c))
        (int alpha))))

(with-primitive-procedures [myfactor]
 (defopt svm-opt [] [c alpha epsilon]
   (let [c (sample (uniform-discrete 0 25))
         alpha (sample (uniform-discrete 0 14))
         epsilon (sample (uniform-discrete 0 4))
         svm-val (svm-eval c alpha epsilon)
         prior-correction (log 1400)]
     (observe (myfactor) (+ svm-val prior-correction)))))


(defn svm-test [n-steps common-name n-test]
  (def opt-results-svm
    (doopt :importance
           svm-opt
           []
           1
           :bo-verbose true
           :bo-debug-folder (str "SVM" common-name n-test)))
  (mapv #(first %) (doall (take n-steps opt-results-svm)))) ; n-steps = 100

;;; Hartmann-6d ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def A
  [[10 3 17 3.5 1.7 8]
   [0.05 10 17 0.1 8 14]
   [3 3.5 1.7 10 17 8]
   [17 8 0.05 10 0.1 14]])

(def P
  [[0.1312 0.1696 0.5569 0.0124 0.8283 0.5886]
   [0.2329 0.4135 0.8307 0.3736 0.1004 0.9991]
   [0.2348 0.1451 0.3522 0.2883 0.3047 0.6650]
   [0.4047 0.8828 0.8732 0.5743 0.1091 0.0381]])

(def alpha
  [1 1.2 3 3.2])

(defn hartman-6d [x1 x2 x3 x4 x5 x6]
  (let [x [x1 x2 x3 x4 x5 x6]
        dxP (mat/pow
             (mat/sub P x)
             2)
        terms (mat/transpose
               (mat/mul A dxP))
        terms-reduced (reduce mat/add terms)
        exp-terms (mat/exp
                   (mat/sub 0 terms-reduced))
        f (- 0
             (reduce + (mat/mul alpha exp-terms)))]
    (- f)))

(with-primitive-procedures [myfactor hartman-6d]
 (defopt h6 [] [x1 x2 x3 x4 x5 x6]
   (let [x1 (sample (uniform-continuous 0 1))
         x2 (sample (uniform-continuous 0 1))
         x3 (sample (uniform-continuous 0 1))
         x4 (sample (uniform-continuous 0 1))
         x5 (sample (uniform-continuous 0 1))
         x6 (sample (uniform-continuous 0 1))
         f (hartman-6d x1 x2 x3 x4 x5 x6)
         prior-correction 0]
     (observe (myfactor)
              (+ (hartman-6d x1 x2 x3 x4 x5 x6)
                 prior-correction)))))

(defn h6-test [n-steps common-name n-test]
  (def h6-results3
    (doopt :importance
           h6
           []
           1
           :bo-verbose true
           :bo-debug-folder (str "hartman" common-name n-test)))
  (mapv #(first %) (doall (take n-steps h6-results3)))) ; n-steps = 200

;;; Execution code ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def n-steps
  {:branin 200
   :lda 50
   :svm 100
   :h6 200})

(defn run-tests [common-name n-test]
  (do
    (lda-test (:lda n-steps) common-name n-test)
    (svm-test (:svm n-steps) common-name n-test)
    (h6-test (:h6 n-steps) common-name n-test)
    (branin-test (:branin n-steps) common-name n-test)
    ))

(defn -main [folder-name n-run]
  (let [_ (println :in)
        folder-name (str folder-name "-" n-run "-" (System/currentTimeMillis))
        n-run (read-string n-run)
        _ (println :n-run n-run)]
      (run-tests folder-name n-run)))

;; (-main "dud-opt" "1")
