# Load Balancer

[![Build Status](https://travis-ci.org/BujakiAttila/LoadBalancer.svg?branch=master)](https://travis-ci.org/BujakiAttila/LoadBalancer)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=BujakiAttila_LoadBalancer&metric=alert_status)](https://sonarcloud.io/dashboard?id=BujakiAttila_LoadBalancer)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=BujakiAttila_LoadBalancer&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=BujakiAttila_LoadBalancer)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=BujakiAttila_LoadBalancer&metric=coverage)](https://sonarcloud.io/dashboard?id=BujakiAttila_LoadBalancer)

A simple load balancer implementation in Java with support for limitation of concurrent requests, health checks and a nice fluent syntax providing a way for dynamically configuring the LoadBalancer instances:

```java
LoadBalancer<String> loadBalancer = LoadBalancerFactory.<String>builder()
    .healthCheckInterval(50)
    .healthCheckIntervalTimeUnit(TimeUnit.SECONDS)
    .maxAcceptedProviders(3)
    .maxNumberOfConcurrentCallsPerProvider(20)
    .scheduling(new RoundRobinScheduler<>())
    .build();

loadBalancer.registerProvider(first);
loadBalancer.registerProvider(second);
loadBalancer.registerProvider(third);

String response1 = loadBalancer.get();  // Response from first
String response2 = loadBalancer.get();  // Response from second
String response3 = loadBalancer.get();  // Response from third
String response4 = loadBalancer.get();  // Response from first
                                        // ...
```

## Health Checks
The health check functionality makes it possible to make sure that the Provider nodes, that are not in a healthy state, are not used anymore. As soon as the Providers are back running and healthy again, they are automatically added back to the list of used nodes.

## Limit concurrent requests
It is also possible to configure the load balancer to be limiting the concurrent requests. This way we can be sure that the Provider nodes will not be overflooded by requests.

## Support for different scheduling strategies
It is also possible to use different scheduling strategies. Random and Round Robin schedulers are supported out of the box, but it is also possible to pass a completely new scheduler strategy.
