# Troc-And-Go

## Developping locally
Requirements:
- Docker
- VSCode
- VSCode's DevContainer extension.

Installation Steps:
- Clone the repository.
- In VSCode, run the command `Open folder in container` and select the repo you just cloned.
- Many installations steps will run to properly setup the environment. Wait until `Done. Press any key to close the terminal.` appears.
- Ensure that the database was successfully created (with an extension or connecting directly with `psql`). DB details are in `./database/db.env`
    - If database is missing, run `./database/init.sh`

---
Launch backend: `./backend/start.sh`

Launch frontend: `./frontend/start.sh`

## Encryption Key
Generating an RSA Key Pair with OpenSSL
To generate a new RSA key pair, use the following commands. Ensure that the keys are stored securely.

Change directory to where the keys should be stored:
cd src/main/resources/jwt

Generate private key:
openssl genpkey -algorithm RSA -out app.key -outform PEM

Generate public key:
openssl rsa -pubout -in app.key -out app.pub

## Credits
Alexandre Gaubert : [https://github.com/Judgy53](https://github.com/Judgy53)

Marc Pourias : [https://github.com/MarcP70](https://github.com/MarcP70)

Nathalie Metreau : [https://github.com/NathalieMet](https://github.com/NathalieMet)

