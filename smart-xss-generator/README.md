# TL;DR

Simple XSS generator based on LSTM

# Install

## Python

    $ pip3 install keras selenium numpy

## Chrome Driver

    $ wget https://chromedriver.storage.googleapis.com/75.0.3770.140/chromedriver_linux64.zip
    $ unzip chromedriver_linux64.zip
    $ mkdir .driver
    $ mv geckodriver .driver/ 

# Run

## Learn model

    $ python3 build_model.py

## Mirror

    $ cd site/
    $ python3 -m http.server --bind 127.0.0.1 8080

## Use model


    $ python tester.py
    Usage: tester.py <url> <model.h5> <payload size: 10 -> Infinity> <Temperature: 0 -> Infinity>
    $ PATH=$PWD/.driver:$PATH python tester.py 'http://localhost:8080/simple-xss.html#' lstm_weights_5.h5 200 0.5

    

# References

 - [XSS Payload List](https://github.com/ismailtasdelen/xss-payload-list)
 - [Natural Language Processing in action, chapter 9](https://www.manning.com/books/natural-language-processing-in-action)
 - [LTSM (Recurrent Neural Network) for Tweet Generation](https://github.com/bcaine/TrumpBot)
