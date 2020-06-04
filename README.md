# Clojurecademy

[Clojurecademy](https://clojurecademy.com/) is an interactive platform that provides Clojure based courses. It's like _Codecademy_ for Clojure.


![Clojurecademy Homepage](/home.png)

## Requirements

* JDK/Clojure 1.8
* Leiningen 2.7.1+
* Datomic Pro Starter Edition 0.9.5561+

## Installation

Create __config.edn__ in _resources_ folder and configure the following map:

```clojure
{:db-uri              "datomic:dev://localhost:4334/your-db"

 :email               {:user     "user"
                       :password "password"
                       :host     "host"}

 :emergency-email     "emergency@mail.com"

 :activation-host     "http://localhost:3000"

 :master-pass         "sha256-master-pass"

 :google-analytics-UA "UA-XXXXXXX-X"}
```

 If you need logging then you need to create __logback.xml__ in _resources_ folder.

### Running

* Run Datomic Transactor
* Run `lein cljsbuild once min-app`
* Run `lein cljsbuild once min-course`
* Run `lein ring server-headless 3000`

### Building

* Run `lein cljsbuild once min-app`
* Run `lein cljsbuild once min-course`
* Run `lein uberjar`

## License

```
MIT License

Copyright 2019 Ertuğrul Çetin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
