# Automated Gmail App
An asynchronous, lightweight and non-blocking app built on Scala using [Gmail Client](https://github.com/rafafrdz/gmail-client) which is built on Cats, Cats-Effects and FS2.

This app automatically sends the message contained in [body](https://github.com/rafafrdz/automated-gmail-app/blob/master/src/main/resources/body.txt) with the keywords `{{keyworkd}}` replaced by the values defined in the csv [table](https://github.com/rafafrdz/automated-gmail-app/blob/master/src/main/resources/table.csv) to the senders indicated in the column `to` of that table.
