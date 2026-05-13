pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "techmovil-app"
        APP_PORT = "8081"
        // Jenkins suele correr en 8080, por eso usamos 8081 para la App
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "Iniciando descarga de código..."
                    checkout scm
                }
            }
        }

        stage('Build & Test') {
            steps {
                echo "Compilando y ejecutando pruebas..."
                // Usamos el wrapper de Maven (mvnw.cmd en Windows)
                bat "./mvnw.cmd clean verify -Ptest"
            }
        }

        stage('JaCoCo Analysis') {
            steps {
                echo "Analizando cobertura de código..."
                // Publicar reporte en Jenkins (requiere plugin HTML Publisher o JaCoCo)
                bat "./mvnw.cmd jacoco:report"
            }
        }

        stage('Package') {
            steps {
                echo "Empaquetando aplicación..."
                bat "./mvnw.cmd package -DskipTests"
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                echo "Construyendo imagen de Docker..."
                // Detener contenedores previos si existen
                bat "docker-compose down || true"
                
                // Construir y levantar con Docker Compose
                bat "docker-compose up -d --build"
                
                echo "Aplicación desplegada exitosamente en http://localhost:${APP_PORT}"
            }
        }
    }

    post {
        always {
            echo "Limpiando espacio de trabajo..."
            // Opcional: archivar artefactos
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        success {
            echo "✅ ¡Pipeline finalizado con éxito!"
        }
        failure {
            echo "❌ El pipeline falló. Revisa los logs anteriores."
        }
    }
}
