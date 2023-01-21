## Sensor Guard - A simple way to protect your sensors from the elements
# University project

Backend Tech Stack
------------------
* [Java Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
* [MyBatis](https://mybatis.org/mybatis-3/) - Persistence framework 
* [MySQL 8](https://www.mysql.com/) - Database 
* [Mailgun](https://www.mailgun.com/) - Email service
* [Docker](https://www.docker.com/) - Containerization

Hosting
------------------
* [Digital Ocean](https://www.digitalocean.com/) - Cloud hosting
* [namecheap](https://www.namecheap.com/) - Domain name
* [Cloudflare](https://www.cloudflare.com/) - DNS
* [Nginx Proxy Manager](https://nginxproxymanager.com/) - Reverse proxy


## Run the project
### local

- change .env.dist to .env and fill in the correct values

```bash
docker-compose up -d
```

### production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Showcase
Webpage: https://sensorguard.systems
API: https://api.sensorguard.systems/api  
Keycloak(Authentication Server): https://keycloak.sensorguard.systems  
