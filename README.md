# nhk-easy-daily 
[![CircleCI](https://circleci.com/gh/Frederick-S/nhk-easy-daily.svg?style=shield)](https://circleci.com/gh/Frederick-S/nhk-easy-daily) [![Build status](https://ci.appveyor.com/api/projects/status/i110dy5pv06etrja/branch/master?svg=true)](https://ci.appveyor.com/project/Frederick-S/nhk-easy-daily/branch/master) [![codecov](https://codecov.io/gh/Frederick-S/nhk-easy-daily/branch/master/graph/badge.svg)](https://codecov.io/gh/Frederick-S/nhk-easy-daily) [![codebeat badge](https://codebeat.co/badges/6495a26e-d97f-4e54-9cb9-92e9332f6413)](https://codebeat.co/projects/github-com-frederick-s-nhk-easy-daily-master) [![Maintainability](https://api.codeclimate.com/v1/badges/f678f3215e4758e0457f/maintainability)](https://codeclimate.com/github/Frederick-S/nhk-easy-daily/maintainability) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Send NHK easy news to Kindle/Evernote (WIP).

## Getting started
### Docker
```sh
docker run -e MYSQL_ROOT_PASSWORD=your-mysql-root-password -e MYSQL_DATABASE=nhk -e MYSQL_USER=your-mysql-user -e MYSQL_PASSWORD=your-mysql-user-password -p 3306:3306 -d mysql:8

docker run -e MYSQL_HOST=ip-address-of-mysql (inspected from docker inspect mysql-container) -e MYSQL_USER=your-mysql-user -e MYSQL_PWD=your-mysql-user-password xiaodanmao/nhk-easy-daily
```

### Docker compose
Run `cp .env.template .env` and modify user&password, then run `docker compose up`.

## License
[MIT](LICENSE)
