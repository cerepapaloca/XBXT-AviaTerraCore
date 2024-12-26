# Rangos
para que se vea el rangos tienes que copiar el carater que lo resepresenta, los carateres que selecionado no son utlizados por eso no tiene icono
- sephirah `🟬`
- developer `🟭`
- seraphine `🟮`
- virtue `🟯`
- arbitro `🟱`
- guardian `🟲`
- mod `🟳`
- builder `🟴`
- passcode `🟵`
- default `🟶`
- partner `🟷`
- streamer `🟸`
- youtuber `🟹`
- tiktoker `🟺`
> [!NOTE]
> Para tener una copia del resourcePack tiene que comprimir en un .zip la carpeta assets
----

# Message.yml
Para resaltar un mensaje usa `<|` para abirir y `|>` para cerrar esto 
hace cambiar el color del mensaje y le añade negrita cuando el mensaje 
está en el discord, también esta `|!>` para cerrar, pero no añade negrita
al mensaje en discord y se usaría cuando cambias a un color de manera 
arbitraria y quieras pasar al color original. Obviamente, puedes usar
`&` para los códigos de color de Minecraft
 
Para utilizar el salto de línea tiene que usar `\n` pero el texto
tiene que estar entre comillas por ejemplo:
```yml
finished: "La prueba a \n finalizado"
```
así da el salto de línea, pero si no tiene comillas no va a funcionar
y si el texto tiene un salto de lina asi:
```yml
premium-validation-failed-log-0: La ip que se envió el paquete no es la 
  misma que se envío el primer paquete por el jugador <|%1$s|> y la 
  ip <|%2$s|>. Discrepancia detectada
```

No tiene efecto, se va a haber todo en una sola línea, pero si tiene `|-` al principio
hace que afecte los salto de lines.

Puedes Modificar `%s` y `%1$s` para asignar los datos, La diferencia es que `%s` no importa el orden de la variable mientras con esta `%1$s` puedes
modificar el orden.