# Stop all containers and rebuild everything from scratch
docker-compose down
docker-compose build --no-cache
docker-compose up -d

#Single command for stopping all containers and rebuilding everything from scratch
docker-compose up -d --build --no-cache

# Stop only the oauth2-admin container
docker-compose stop oauth2-admin

# Remove the old container and image
docker-compose rm -f oauth2-admin
docker rmi oauth2-test-oauth2-admin  # Image name might vary

# Rebuild without cache
docker-compose build --no-cache oauth2-admin

#Restart and rebuild only updated services
docker compose up -d

# Start and rebuild only this service
docker-compose up -d oauth2-admin

# Remove all file and volumes
docker system prune

# Rebuild your image
docker build your-image-name