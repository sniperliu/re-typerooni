# re-typerooni

A re-implementation of [typerooni](https://github.com/domgetter/typerooni) with [re-frame](https://github.com/Day8/re-frame).

## Requirement

Emacs and Cider installed

## Usage

Leinigen

```bash
$ lein figwheel

$ open http://localhost:3449
```

Boot
```bash
$ boot serve -d target watch reload cljs-repl cljs target -d target
```

Emacs

M-x cider-connect

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.