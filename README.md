# memeDB

## Usage

1. Get telegram token from [BotFather](https://telegram.me/BotFather)
2. Clone repo
3. Override application.yml for tg-bot in `./etc/tg-bot` dir
4. Override application.yml for search-engine in `./etc/search-engine` dir (Optional)
5. Up docker compose `docker compose up -d`

### Configuration

**tg-bot:**

```yaml
bot:
  telegramToken: BOT_TOKEN
```

**search-engine:**

```yaml
elasticsearch:
  url: elasticsearch
  user: user
  password: password
```
