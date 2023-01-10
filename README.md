"#backend" 
Ich mach das spaeter aus amk 

## Run the project
### local
```bash
cd deployment
docker-compose up -d
```

### production
```
cd deployment
docker-compose -f docker-compose.prod.yml up -d
```

## live
API: https://api.sensorguard.systems/api
Keycloak(Authentication Server): https://keycloak.sensorguard.systems
Webpage: https://sensorguard.systems
