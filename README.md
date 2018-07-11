# Leadlucky
### Email Marketing Platform

##### Components

- api: Spring-Boot REST api
- ui: Vue.js web portal
- themes: Vue.js themes, customizable via the web portal

##### Run Locally

You wil need to have: 

- MySQL Database
- Clone the themes project
    - Run `npm link` from the themes project
    - Run `npm link @leadlukcy/leadlucky-themes` from the ui directory in this project
- Edit application.yaml to provide correct database and email credentials, and a path to the google api key json
- Google Analytics Secret Key json file

