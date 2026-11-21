# 🌐 Eureka Server (Service Discovery)

- **Projet :** Gestion des Événements Universitaires (ESPRIT - 4DAS6)
- **Équipe :** Warriors
- **Technologies :** Spring Boot 3.3.4, Spring Cloud Netflix Eureka Server 2023.0.3, Java 17/21
- **Rôle :** Annuaire centralisé et découverte dynamique de tous les microservices de la plateforme.

---

## 📌 Fonctionnalités

* Enregistrement automatique et dynamique de chaque microservice dès son démarrage.
* Détection en temps réel de l'état de santé des services (Heartbeats).
* Fourniture des adresses réseau (IP/nom d'hôte et port dynamique) à l'API Gateway et aux clients OpenFeign pour le routage et le load balancing.

---

## 🚀 Comment lancer Eureka Server ?

### Depuis IntelliJ IDEA :
1. Importer le projet comme projet Maven (`pom.xml` > **Maven** > **Reload project**).
2. Lancer la classe principale `EurekaServerApplication.java`.

### Depuis le terminal :
```bash
mvn clean spring-boot:run
```

Une fois démarré, le tableau de bord d'administration Eureka est accessible dans le navigateur pour visualiser en direct tous les microservices enregistrés.

---

## 🤝 Comment enregistrer votre microservice auprès d'Eureka ?

Pour que votre microservice apparaisse sur l'annuaire Eureka, ajoutez dans votre propre projet :

1. **Dépendance dans votre `pom.xml` :**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

2. **Configuration dans votre `application.properties` :**
```properties
spring.application.name=votre-nom-de-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

3. **Annotation sur votre classe principale :**
Ajouter `@EnableDiscoveryClient` au-dessus de votre classe `@SpringBootApplication`.
