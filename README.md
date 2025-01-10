# Troc-And-Go

Generating an RSA Key Pair with OpenSSL
To generate a new RSA key pair, use the following commands. Ensure that the keys are stored securely.

Change directory to where the keys should be stored:
cd src/main/resources/jwt

Generate private key:
openssl genpkey -algorithm RSA -out app.key -outform PEM

Generate public key:
openssl rsa -pubout -in app.key -out app.pub
