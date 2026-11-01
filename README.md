# Fire TV Stick Launcher HD

Tela inicial personalizada para o **Amazon Fire TV Stick HD** (Fire OS 8, baseado no Android 11 / API 30).

- Papel de parede próprio (logo Fire TV Stick, stick e controle, luzes azul e laranja) sobre fundo azul-marinho escuro
- Ícones em grade organizada, 2 linhas visíveis, em cartões translúcidos (colunas calculadas conforme a tela; pensado para 1280x720)
- Relógio (HH:mm) e data no canto superior direito, no idioma do aparelho
- Foco nos cartões com borda laranja e leve zoom
- Atalhos para todos os aplicativos instalados (apps de TV primeiro, depois os demais, em ordem alfabética)
- Navegação só com o controle: **setas** movem o foco, **OK** abre o app, **segurar OK** abre as informações do app
- A lista se atualiza sozinha quando você instala, remove ou atualiza um app

> **Não altera nem desativa o launcher da Amazon.** O app é instalado como um aplicativo comum. O botão Home continua levando para a tela inicial original.

## Estrutura

```
app/src/main/
├── AndroidManifest.xml              # entrada LAUNCHER/LEANBACK_LAUNCHER (e alias HOME desligado)
├── java/com/firetvlauncher/hd/
│   ├── MainActivity.kt              # tela, relógio/data, abrir apps, atualização da lista
│   ├── AppAdapter.kt                # grade com foco (borda azul + zoom)
│   ├── AppRepository.kt             # lê os apps instalados
│   └── AppInfo.kt
└── res/                             # layouts, cores, banner e ícone
    └── drawable-nodpi/wallpaper.jpg # papel de parede (1280x720)
```

## Trocar o papel de parede

Substitua o arquivo `app/src/main/res/drawable-nodpi/wallpaper.jpg` por outra imagem **16:9** (1280x720 ou 1920x1080), mantendo o mesmo nome, e faça commit. Dicas:

- Deixe a **metade de cima** com o visual (logo, produto, luzes) e a **metade de baixo escura e lisa**, porque é onde ficam os apps.
- Evite desenhar relógio ou atalhos na imagem: o app já mostra os reais.
- Prefira JPG com até uns 300 KB; a Fire TV Stick HD tem pouca memória.
- Se mudar a altura da área do topo, ajuste `hero_height` em `app/src/main/res/values/dimens.xml`.

> **Sem Android Studio ou com computador fraco?** Veja o [GUIA-GITHUB-ACTIONS.md](GUIA-GITHUB-ACTIONS.md): o GitHub compila o APK na nuvem pelo navegador, usando o workflow em `.github/workflows/android.yml`.

## 1. Preparar o computador

Duas opções para gerar o APK:

**A) Android Studio (mais fácil)**: instale o Android Studio (versão recente) e abra a pasta do projeto. Ele baixa o SDK e o Gradle sozinho.

**B) Linha de comando**: JDK 17 ou superior e o Android SDK (plataforma 34 + build-tools) instalados, com a variável `ANDROID_HOME` apontando para o SDK.

## 2. Gerar o APK

No Android Studio: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

Na linha de comando (dentro da pasta do projeto):

```
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

O arquivo sai em `app/build/outputs/apk/debug/app-debug.apk` (já assinado com a chave de debug, suficiente para instalar na sua TV).

## 3. Preparar o Fire TV Stick HD

1. **Configurações > Minha Fire TV > Sobre**: selecione o nome do aparelho (Fire TV Stick) e aperte OK 7 vezes até aparecer "Você agora é um desenvolvedor".
2. **Configurações > Minha Fire TV > Opções do desenvolvedor**: ative **Depuração ADB** e **Apps de fontes desconhecidas**.
3. Anote o IP da TV em **Minha Fire TV > Sobre > Rede**.

O computador e o Fire Stick precisam estar na mesma rede Wi-Fi.

## 4. Instalar e abrir

Com o `adb` instalado no computador (vem no Android SDK, pasta `platform-tools`):

```
adb connect IP_DA_TV:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.firetvlauncher.hd/.MainActivity
```

Na primeira conexão, a TV mostra um pedido para autorizar o computador: marque "Sempre permitir" e confirme.

Depois disso o app também aparece em **Seus aplicativos e canais** com o nome "Fire TV Stick Launcher HD".

## 5. O que conferir

| Teste | Esperado |
|---|---|
| Abrir o app | Fundo preto/azul escuro, relógio e data no topo, grade de ícones |
| Primeiro foco | O primeiro cartão já vem destacado (borda azul) |
| Setas do controle | O foco anda entre os cartões e a grade rola quando passa da tela |
| OK | Abre o aplicativo selecionado |
| Segurar OK | Abre a tela de informações do app (nas configurações) |
| Voltar | Sai do launcher e volta para onde você estava |
| Home | Vai para a tela inicial **original da Amazon** |
| Instalar/remover um app | A grade se atualiza sozinha em instantes |
| Bordas da tela | Nada cortado (há margem de área segura para TV) |

Se algum ícone ou texto parecer pequeno/grande demais na sua TV, ajuste `app/src/main/res/values/dimens.xml` (`icon_size`, `card_height`, `card_min_width`).

## Testar sem a TV (opcional)

No Android Studio, crie um emulador em **Device Manager > Create Device > TV > Android TV (720p)** com imagem de sistema API 30. Ele tem a mesma resolução 1280x720 e aceita as setas do teclado e Enter como controle remoto. O emulador não tem os apps da Amazon, então a grade mostra só os apps do próprio emulador.

## Desinstalar

```
adb uninstall com.firetvlauncher.hd
```

## Opcional (avançado): usar como tela inicial

O manifesto traz um `HomeAlias` **desligado**. Ele só existe para uma etapa futura e **não é necessário para testar**. Se um dia quiser experimentar, e só depois de validar tudo acima:

```
adb shell pm enable com.firetvlauncher.hd/.HomeAlias     # liga
adb shell pm disable com.firetvlauncher.hd/.HomeAlias    # desfaz
```

O comportamento do botão Home varia entre versões do Fire OS e não foi testado neste projeto.
