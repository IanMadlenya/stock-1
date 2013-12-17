__author__ = 'shijieru'

import json as simplejson
from dajaxice.decorators import dajaxice_register
from datetime import datetime
import subprocess
from subprocess import CalledProcessError
import os


@dajaxice_register
def predict(request, symbol):
    try:
        subprocess.call(['java', '-jar', '/Users/shijieru/Desktop/candleFeature.jar', str(symbol), datetime.now().strftime("%Y-%m-%d"),'/Users/shijieru/Desktop/predictresult.arff'])
    except CalledProcessError as e:
        return simplejson.dumps({'message':'Future change direction is %s!' % str(e.output)})
    command = "java -classpath /Users/shijieru/Desktop/weka.jar weka.classifiers.rules.JRip -T /Users/shijieru/Desktop/predictresult.arff -c first -l /Users/shijieru/Desktop/Finalfold3.model -p 0"
    p = os.popen(command)
    tokens = p.read().split(":")
    tokens = tokens[2].split(" ")
    return simplejson.dumps({'label':'%s' % tokens[0]})
