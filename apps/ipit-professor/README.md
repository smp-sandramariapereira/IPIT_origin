# IPIT Professor — aplicativo Android
Primeira versão funcional offline para o professor planejar e acompanhar uma aplicação do IPIT.

## Recursos
- Planejamento curricular salvo no dispositivo.
- Percurso introdutório em cinco momentos e consulta ao percurso técnico sistematizado em oito etapas.
- Checklist de condução e registros de evidência por etapa.
- Rubrica breve por equipe identificada por código.
- Orientação conversacional **guiada por regras**, com perguntas contextualizadas. Não é IA generativa.
- Exportação/compartilhamento e restauração de backup em JSON.
- Exclusão local e aviso de privacidade.
- Interface adaptada a celular e tablet.

## Executar a interface no computador
Com Node.js 20 ou superior, nesta pasta:
```bash
node scripts/serve.mjs
node --test test/core.test.cjs
```
Abra http://127.0.0.1:4180. Os dados ficam neste navegador. Android e navegador têm armazenamentos independentes.

## Gerar Android
Abra esta pasta no Android Studio, com JDK 17, SDK 36 e Gradle 8.13. O projeto usa AGP 8.13.2. Não há Gradle Wrapper binário nesta entrega; gere com uma instalação confiável do Gradle:
```bash
gradle wrapper --gradle-version 8.13
./gradlew assembleDebug
./gradlew bundleRelease
```
O APK de teste fica em app/build/outputs/apk/debug/. O bundle release sem assinatura não pode ser enviado à loja: gere um Android App Bundle **assinado** pelo Android Studio.
O applicationId provisório é br.com.ipit.professor; confirme a identidade antes da primeira publicação, pois alterações posteriores exigem um aplicativo distinto.

## Referência Luma
Modelo consultado: [luma-tutor-solana-dev](https://github.com/smp-sandramariapereira/luma-tutor-solana-dev), commit f05c035a5849ba309e598efff34de3e4c9c56617. Inspiração: objetivo declarado, orientação curta, pergunta e opções de continuidade, separação entre interface e tutor.
Implementação independente: não copia código, imagens, marcas ou prompt do Luma, cujo README informa licença ainda não formalizada.

## Limites da entrega
O projeto Android não foi compilado neste ambiente por ausência de SDK e Gradle. A interface e regras têm testes locais. Não há APK/AAB assinado, publicação na loja, servidor de IA, login, cobrança ou sincronização.
Leia [publicação](docs/GOOGLE-PLAY.md), [privacidade](docs/PRIVACIDADE.md) e [evolução de IA](docs/IA.md).

Conteúdo educacional CC BY 4.0 e código MIT conforme a LICENSE do IPIT_origin. Autora: Sandra Maria Pereira.
**A pasta está em repositório público** e herda seu licenciamento. Componentes comerciais fechados devem ficar em repositório privado separado.
