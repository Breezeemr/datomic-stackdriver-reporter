# datomic-stackdriver-reporter

A tiny clojure library that reports datomic metrics to stackdriver metrics. This is heavily inspired by [Tom Crayford](https://github.com/tcrayford)'s [datomic-riemann-reporter](https://github.com/yeller/datomic-riemann-reporter/).

## Usage

Drop an uberjar in $DATOMIC_DIR/lib, then add this to your transactor's `properties` file:

```ini
metrics-callback=com.breezeehr.datomic.reporter/transactor
```
Then restart your transactor, and you'll see events showing up in riemann. All
events will be tagged "datomic", and start with "datomic". Event names come
from the metrics available on http://docs.datomic.com/monitoring.html.

## License

Copyright © 2015 Canary Computer Corporation

Distributed under the Apache License version 2.0
