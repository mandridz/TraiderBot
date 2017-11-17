TraiderBot
==============

Place config.properties file to root folder:

# Имя пользователя для биржи
stockName=<User Name>

# Ключ для биржи
stockKey=<Key>

# Секрет для биржи
stockSecret=<Secret>

# Выводить информацию в ThingSpeak (0 - нет, 1 - да)
isSendToThingSpeak=1

# Номер канала в ThingSpeak
thChannelId=<Channel Id>

# Write-ключ ThingSpeak
thWriteKey=<Key>

# Максимальное количество валют для ордеров на покупку
currencyPairsLimit=5

#Минимальный профит, процентах
profit=2

# Минимальный размер ордера, в BTC (Poloniex)
orderMin=0.005

# Интервал запуска программы, в минутах
shedulerTimeout=2

# Нижняя граница стоимости валют, которые подбираются для ордеров на покупку
satoshiMin=100000

# Максимальное количество дней, которое валюта лежит в кошельке (затем валюта продается по рыночной цене)
timeLimit=14

# Какую сумму в BTC-кошельке можно использовать для торговли, в процентах
btcLimit=100
