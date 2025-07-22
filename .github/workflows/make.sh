#!/bin/bash

echo "Getting started"

# Bundle docs into zero-dependency HTML file
npx @redocly/cli build-docs ./openapi/simple-auth0.yaml && \
mv redoc-static.html index.html && \
echo "Changed name from redoc-static.html to index.html" && \
# Add favicon
#sed -i '7 i \ \ <link rel="icon" type="image/png" href="images/favicon.png"/>' index.html && \
echo -e "\nDone!"
