#!/bin/bash
# Script to build and push the Event Service Docker image

# Set variables
IMAGE_NAME="eventhub/event-service"
IMAGE_TAG=${1:-latest}  # Use provided tag or default to "latest"
REGISTRY=${2:-""}       # Optional registry prefix

# Display build information
echo "Building Docker image: $IMAGE_NAME:$IMAGE_TAG"
echo "========================================"

# Build the application with Gradle
echo "Building application with Gradle..."
./gradlew clean bootJar

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "Gradle build failed! Exiting."
    exit 1
fi

# Build the Docker image
echo "Building Docker image..."
docker build -t $IMAGE_NAME:$IMAGE_TAG .

# Check if image build was successful
if [ $? -ne 0 ]; then
    echo "Docker build failed! Exiting."
    exit 1
fi

# Tag with registry if provided
if [ ! -z "$REGISTRY" ]; then
    REGISTRY_IMAGE="$REGISTRY/$IMAGE_NAME:$IMAGE_TAG"
    echo "Tagging image for registry: $REGISTRY_IMAGE"
    docker tag $IMAGE_NAME:$IMAGE_TAG $REGISTRY_IMAGE

    echo "Pushing image to registry: $REGISTRY_IMAGE"
    docker push $REGISTRY_IMAGE
fi

echo "========================================"
echo "Done! Image built successfully: $IMAGE_NAME:$IMAGE_TAG"
echo "Run with: docker-compose up -d"