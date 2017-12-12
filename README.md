# linebot-sls-bp-kotlin

This application is a LINE BOT written in Kotlin running on AWS Lambda, and used [Serverless Framework](https://serverless.com/framework/)

And based on [moritalous/linebot\-serverless\-blueprint\-java](https://github.com/moritalous/linebot-serverless-blueprint-java).

## functions

### Response Echo Message

You can receive same message what you said.

ex) you said:'Hello.', bot said:'Hello.'

### Send Push Message

By default, you can receive "Hello World!!" message at 00:00(GMT) on weekdays.

If you want to change theses setting, edit below content.

```yaml
push:
  events:
    - schedule:
      rate: cron(0 0 ? * MON-FRI *)
      input:
        message: 'Hello World!!'
```

## Usage

### Create config files

```bash
# for development
$ cp ./config/dev/environment.sample.yml ./config/dev/environment.yml
# for production
$ cp ./config/prod/environment.sample.yml ./config/prod/environment.yml
```

### Set your keys in above files

```yml
CHANNEL_SECRET: FOO
CHANNEL_ACCESS_TOKEN: BAR
USER_ID: BAZ
TABLE_NAME: QUX
```

You must to definish different `TABLE_NAME` value between `./config/dev/environment.yml` and `./config/prod/environment.yml`.

### Deploy on your AWS!!

```bash
$ npm run deploy
```

## Reference

* [moritalous/linebot\-serverless\-blueprint\-java](https://github.com/moritalous/linebot-serverless-blueprint-java)
* [linebot\-serverless\-blueprint\-javaを作った！](https://qiita.com/moritalous/items/af4f05543a1b8817e472)
