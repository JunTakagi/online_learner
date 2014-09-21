EVAL=$1
REPEAT=$2
METHOD=$3
OPTS=$4

java -cp target/online-0.0.1.jar jtakagi.online.experiment.Experiment $EVAL testdata/a9a.txt testdata/a9a.t $REPEAT $METHOD $OPTS
