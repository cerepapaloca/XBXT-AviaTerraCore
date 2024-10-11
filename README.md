> [!IMPORTANT]
> Todo lo que implique las base datos hagalo en un hilo aparte por que si no el hace BOON!!
> lo que tiene que poner
> ```java
> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
>       //toda la logica que tenga una base de tados             
> });
> ````
