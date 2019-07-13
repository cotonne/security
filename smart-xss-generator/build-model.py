import numpy as np

with open("data.txt", "r") as f:
  text = f.read()

chars = sorted(list(set(text)))
char_indices = dict([(c, i) for i, c in enumerate(chars)])
indices_char = dict([(i, c) for i, c in enumerate(chars)])
print('corpus length: {}  total chars: {}'.format(len(text), len(chars)))

# Assembly a training set
MAX_LENGTH_PAYLOAD=50
step = 2
sentences = []
next_chars = []
for i in range(0, len(text) - MAX_LENGTH_PAYLOAD, step):
  sentences.append(text[i: i + MAX_LENGTH_PAYLOAD])
  next_chars.append(text[i + MAX_LENGTH_PAYLOAD])
print('nb sequences:', len(sentences))


# one-hot encode the training example
X = np.zeros((len(sentences), MAX_LENGTH_PAYLOAD, len(chars)), dtype=np.bool)
y = np.zeros((len(sentences), len(chars)), dtype=np.bool)
for i, sentence in enumerate(sentences):
  for t, char in enumerate(sentence):
    X[i, t, char_indices[char]] = 1
    y[i, char_indices[next_chars[i]]] = 1

# Assemble a character-based LSTM model for generating text
from keras.models import Sequential
from keras.layers import Dense, Activation, Dropout
from keras.layers import LSTM
from keras.optimizers import RMSprop
model = Sequential()
model.add(LSTM(256, input_shape=(MAX_LENGTH_PAYLOAD, len(chars))))
model.add(Dropout(0.2))
model.add(Dense(len(chars)))
model.add(Activation('softmax'))
optimizer = RMSprop(lr=0.01)
model.compile(loss='categorical_crossentropy', optimizer=optimizer)
model.summary()

# Save the model
model_structure = model.to_json()
with open("lstm_model.json", "w") as json_file:
  json_file.write(model_structure)

# TRAIN
epochs = 6
batch_size = 128
for i in range(5):
  model.fit(X, y, batch_size=batch_size, epochs=epochs)
  model.save_weights("shakes_lstm_weights_{}.h5".format(i+1))

