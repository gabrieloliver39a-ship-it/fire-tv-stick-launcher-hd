# Compilar o APK na nuvem (GitHub Actions)

Guia para quem não tem Android Studio: o GitHub compila o projeto nos servidores dele e você só baixa o APK. Seu notebook (Windows 8.1, 32 bits, 2 GB de RAM) precisa apenas de um **navegador**. Nada de Java, Gradle ou SDK no seu computador.

> O app **não remove nem desativa o launcher da Amazon**. Ele é instalado como um aplicativo comum, e o botão Home continua levando para a tela inicial original.

## O que já está configurado no projeto

O arquivo `.github/workflows/android.yml` faz tudo sozinho:

| Item | Versão |
|---|---|
| Java | JDK 17 (Temurin) |
| Gradle | 8.9 (baixado pelo `gradlew`, definido em `gradle/wrapper/gradle-wrapper.properties`) |
| Plugin Android do Gradle | 8.5.2 |
| Kotlin | 1.9.24 |
| Android SDK | plataforma 34 + build-tools 34.0.0 |

Essas versões são compatíveis entre si. O alvo do app continua sendo o Fire OS 8 (Android 11, API 30).

## 1. Navegador para o Windows 8.1 de 32 bits

O Chrome e o Edge atuais não rodam mais no Windows 8.1. Use o **Firefox ESR 115**, que a Mozilla mantém para Windows 7/8/8.1, inclusive 32 bits, com atualizações previstas até março de 2027. Baixe a versão "Firefox ESR" (32 bits) em mozilla.org/firefox/enterprise/. Com 2 GB de RAM, feche os outros programas e deixe só uma aba do GitHub aberta.

## 2. Criar conta e repositório

1. Crie uma conta gratuita em github.com (precisa de e-mail).
2. Clique em **New repository** (botão verde ou o **+** no topo).
3. Nome: `fire-tv-stick-launcher-hd`.
4. Marque **Public**. O projeto não tem nenhum dado pessoal, e o repositório público permite o link direto de download usado no passo 6.
5. Clique em **Create repository**.

## 3. Enviar o projeto

1. No Windows, clique com o botão direito no `fire-tv-stick-launcher-hd.zip` e escolha **Extrair tudo**.
2. No repositório recém-criado, clique em **uploading an existing file**.
3. Abra a pasta extraída e **arraste o conteúdo dela** (as pastas `.github`, `app`, `gradle` e os arquivos soltos como `build.gradle.kts`, `gradlew`, `settings.gradle.kts`) para a página do navegador. Não arraste a pasta de fora, senão tudo fica um nível abaixo e o build não encontra o projeto.
4. Espere terminar de subir e clique em **Commit changes**.

Confira que existe o caminho `.github/workflows/android.yml` no repositório. Se a pasta `.github` não subiu:

- clique em **Add file > Create new file**;
- no campo do nome digite `.github/workflows/android.yml` (as barras criam as pastas);
- cole o conteúdo do arquivo `COPIA-DO-WORKFLOW/android.yml` (uma cópia idêntica, em pasta comum, que vem no ZIP justamente para esse caso) e clique em **Commit changes**.

Depois de criar o arquivo, a pasta `COPIA-DO-WORKFLOW` pode ficar no repositório; ela não faz nada sozinha, só o que está em `.github/workflows` é executado.

## 4. Compilar

O build começa sozinho assim que o commit termina. Para acompanhar:

1. Abra a aba **Actions** do repositório.
2. Clique na execução **Build APK** (bolinha amarela = rodando, verde = pronto, vermelho = erro).
3. A primeira vez leva uns 5 a 10 minutos, porque baixa as dependências. As próximas são mais rápidas.

Para rodar de novo sem mudar nada: **Actions > Build APK > Run workflow**.

## 5. Baixar o APK

1. Em **Actions**, clique na execução com o ✔ verde.
2. Role até o fim da página, na seção **Artifacts**.
3. Clique em **FireTvStickLauncherHD-apk**. Vai baixar um `.zip` (precisa estar logado no GitHub).
4. Extraia o zip: dentro está o **`FireTvStickLauncherHD.apk`**.

Os artifacts ficam guardados por 30 dias.

## 6. Instalar no Fire TV Stick sem computador (recomendado)

Esse caminho evita o `adb`, que pode não funcionar no Windows de 32 bits.

**a) Publicar uma versão com link fixo**

1. No repositório, abra **Releases > Draft a new release** (ou **Create a new release**).
2. Em **Choose a tag**, digite `v1.0.0` e escolha **Create new tag on publish**.
3. Dê um título (por exemplo, "Versão 1.0.0") e clique em **Publish release**.
4. Isso dispara o build de novo e, ao terminar (veja em Actions), o APK aparece anexado na release.

O link direto do APK fica assim (troque `USUARIO` pelo seu nome no GitHub):

```
https://github.com/USUARIO/fire-tv-stick-launcher-hd/releases/latest/download/FireTvStickLauncherHD.apk
```

**b) No Fire TV Stick**

1. Instale o app **Downloader** (da AFTVnews) pela loja de apps da Amazon.
2. Ative o modo desenvolvedor, se ainda não fez: *Configurações > Minha Fire TV > Sobre*, selecione o nome do aparelho e aperte OK 7 vezes.
3. Em *Minha Fire TV > Opções do desenvolvedor > Instalar apps desconhecidos* (ou *Apps de fontes desconhecidas*), ative a permissão para o **Downloader**.
4. Abra o Downloader, digite o link acima, baixe e escolha **Instalar**.
5. Abra pelo próprio instalador ou em **Seus aplicativos e canais > Fire TV Stick Launcher HD**.

Para atualizar depois: publique uma nova release (`v1.0.1`, `v1.0.2`...) e repita o download no Downloader. O link `latest` sempre aponta para a mais recente.

## 7. Se o build der erro (bolinha vermelha)

1. Em **Actions**, clique na execução vermelha e depois no job **build**.
2. Abra o passo marcado com ✖ (normalmente **Compilar APK**) e copie as linhas de erro, geralmente as que começam com `e:` ou `error:`.
3. Me envie esse trecho que eu corrijo o código. Além disso, a execução guarda uma pasta de relatórios em **Artifacts > relatorios-de-erro**.

Esta é a primeira vez que o projeto é compilado de verdade, então é possível que apareça algum erro pequeno de código ou de configuração.

## 8. Alterar o projeto depois

Você pode editar qualquer arquivo direto no navegador: abra o arquivo no repositório, clique no lápis, altere e faça **Commit changes**. Cada commit na branch `main` gera um novo APK automaticamente.
