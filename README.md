# Automated Gmail App

**Author**: Rafael Fernandez\
**Web**: [https://rafaelfernandez.dev/](https://rafaelfernandez.dev/) \
**License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) \
**Repository**: [rafafrdz/automated-gmail-app](https://github.com/rafafrdz/automated-gmail-app)

## What is AG App?

An asynchronous, non-blocking app built in Scala and using [Gmail Client](https://github.com/rafafrdz/gmail-client) (built on top of Cats, Cats-Effects and fs2).

This app automatically sends the message contained in a `txt`, `html`, or a *Google Drive* document with the keywords `{{keyworkd}}` containing them substituted by the values defined in an `csv` table or in a `spreadsheet` document of *Google Drive* to the senders indicated in the to column of that table / *spreadsheet*.

# Quick guide

We must make sure that we previously meet the requirements of this application. The requirements can be found in the **Requirements** section.

### Using local documents

1. Create a `txt` document and save it in the `app` folder. Example: `body.txt`. \
Remember that the document must follow the following format: **Message format**.


2. Create a `csv` document and save it in the `app` folder. Example: `table.csv`.\
Remember that the document must follow the following format: **Table format**

3. Modify the `application.conf` or simply `application` file in the `app/settings` folder, adding the document name to the `path.txt` property and the csv name to the `path.table` property.\
The different properties that you can use in the configuration can be found in the **Settings** section. You should have a configuration similar to this:

```
path.table = "table.csv".

path.txt = "body.txt"

```

4. Execute the `execute` file

### Using documents in *Google Drive*.

1. Create a *Google Drive* document.\
Remember that the document must follow the following format: **Message format**.

2. Create a `spreadsheet` or `Spreadsheet` document from *Google Drive*.\
Remember that the document must follow the following format: **Table format**.

3. Modify the `application.conf` or simply `application` file in the `app/settings` folder, adding the document url to the `path.txt` property and the *spreadsheet url to the `path.table`.property.\
The different properties that you can use in the configuration can be found in the **Configuration** section. You should have a configuration similar to this:

```
path.table = "https://docs.google.com/spreadsheets/d/1wv70qW2qQjCq2taYARQxan44r/edit?usp=share_link"

path.txt = "https://docs.google.com/document/d/1h62rKI0NWlA2Mdok-t8YRWXdhb/edit?usp=share_link"

```

4. Run the `execute` file

# Requirements

* **(Windows 10 / 11) Install WSL**.\
Doc [https://learn.microsoft.com/en-us/windows/wsl/install#install-wsl-command](https://learn.microsoft.com/en-us/windows/wsl/install#install-wsl-command).

* **Docker and Docker-Compose must be installed**.\
Doc. (Windows 10 / 11) [https://docs.docker.com/desktop/install/windows-install/](https://docs.docker.com/desktop/install/windows-install/)

* **(Recommended) Create a Gmail application password**.\
Doc. [https://support.google.com/accounts/answer/185833?hl=es#zippy=](https://support.google.com/accounts/answer/185833?hl=es#zippy=)

* **Dispose of the `automated-gmail-app.zip` file**.\
This is where the application is located. It can also be downloaded from the [rafafrdz/automated-gmail-app](https://github.com/rafafrdz/automated-gmail-app) repository.


### **Gmail application passwords**.

**Gmail application passwords** are generated passwords that can serve as your own personal password to access Gmail. It is recommended that you use these so that you do not provide your personal password to third-party applications.

However, just as if they were your personal password, these are still passwords for accessing your Gmail account, so it is important that:

1. **Do not give your Gmail application password** to just anyone, think of it as your personal password.

2. **Renew your Gmail application password** frequently.

3. Once you stop using the application, it is recommended that you **delete the Gmail application password** you created.