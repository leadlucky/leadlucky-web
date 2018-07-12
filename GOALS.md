# Goals

TODO - Convert to Issues in GitHub

1.0: MVP
   - ~~Customizable pages~~
   - ~~Email collection~~
   - ~~Theme framework~~
   - Analytics

1.1: Scalability for User Pages
   - Make user pages seperate Vue app from the portal
   - SSR for user pages; don't load *every* theme in bundle
   - nginx loadbalancer in front of user pages app instance(s)

1.2: Automailer
   - Determine pricing
   - Stand up or rent SMTP Servers
   - Email Template Creation
   - Mail Scheduling

1.3: Containerization
   - Make seperate microservice for serving user page data, collecting emails
   - Make seperate microservice for analytics
   - Make seperate microservice for managing pages, getting collected emails
   - Integrate Spring Cloud for API gateway
   - Dockerize all services
   - Choose orchestration tool/platform for services

1.4: Better URLs
   - Subdomain links to user pages (nginx url rewrites)
     - idea: rewrite from `{pagename}.ll.io` to `{pages-backend}/pagename`
   - BYO Domain Name

1.5: BYO Google Analytics
   - Allow users to enter their analytics key
   - Track page view and email submit events
   - _STRETCH_: Use embed API to show metrics in LL portal

1.6: Website Builder
   - Drag/Drop website builder
   - Main Vuetify components available:
    - Cards, Buttons, Images, etc.

