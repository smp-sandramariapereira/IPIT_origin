# IPIT Professor — aplicativo Android
Versão 0.2.1: agente conversacional local com API opcional para o professor planejar e acompanhar uma aplicação do IPIT.

## Recursos
- Planejamento curricular salvo no dispositivo.
- Percurso introdutório em cinco momentos e consulta ao percurso técnico sistematizado em oito etapas.
- Checklist de condução e registros de evidência por etapa.
- Rubrica breve por equipe identificada por código.
- Mensagens livres para o agente, com respostas locais por regras restritas ao IPIT; fora do contexto, orienta reformular.
- Conexão opcional, somente no Android, a API compatível com Chat Completions. Modelo e chave próprios; sem chave no código ou backup.
- Contexto do planejamento e últimas seis mensagens são enviados ao provedor apenas após ativação consentida.
- PDF paginado do plano, evidências e avaliações: salvar no dispositivo, enviar ao WhatsApp ou compartilhar com outro aplicativo.
- Exportação/compartilhamento em texto e restauração de backup em JSON.
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
O projeto Android não foi compilado neste ambiente por ausência de SDK e Gradle. A interface e regras têm testes locais. Não há APK/AAB assinado, publicação na loja, backend próprio, login, cobrança ou sincronização. A conexão de API nativa foi implementada, mas não testada em Android nem com provedor real neste ambiente.
No modo local, as respostas são limitadas a orientações documentadas por regras. API amplia exemplos e adaptações dentro do IPIT; instruções de escopo não garantem contenção absoluta do modelo.

Leia [publicação](docs/GOOGLE-PLAY.md), [privacidade](docs/PRIVACIDADE.md) e [evolução de IA](docs/IA.md).

Conteúdo educacional CC BY 4.0 e código MIT conforme a LICENSE do IPIT_origin. Autora: Sandra Maria Pereira.
**A pasta está em repositório público** e herda seu licenciamento. Componentes comerciais fechados devem ficar em repositório privado separado.

## Documento PDF
Na área Dados, use Salvar PDF para escolher pasta/arquivo pelo seletor Android. Para WhatsApp, o aplicativo abre a tela de compartilhamento; o professor escolhe contato e confirma. Se WhatsApp e WhatsApp Business estiverem ausentes, abre o seletor de outros aplicativos. Nenhum envio é automático. Não é necessário conceder acesso amplo ao armazenamento.
