# Preparação para Google Play
Esta entrega é código-fonte de um protótipo Android; não é aplicativo publicado nem AAB assinado.

## Build e teste
AGP 8.13.2, Gradle 8.13, JDK 17 e SDK 36. CompileSdk e targetSdk 36; minSdk 26.
Configure o SDK local sem publicar local.properties. Gere o Gradle Wrapper com Gradle 8.13 confiável e mantenha o wrapper no desenvolvimento posterior.
Teste APK debug em celulares e tablets: teclado, rotação, botões, TalkBack, aumentar fonte, offline, salvar/reabrir, exclusão, compartilhar e restaurar.
Confirme o applicationId br.com.ipit.professor antes de publicar.
Crie a chave de assinatura fora do repositório; guarde cópia segura. No Android Studio: Build → Generate Signed App Bundle or APK → Android App Bundle.
Não publique chaves ou senhas. A chave de assinatura não foi criada nesta entrega.

## Loja
- Conta de desenvolvedor da responsável e verificação exigida pela plataforma.
- Nome, descrição, ícone final, screenshots de uso real e contato.
- Política de privacidade pública com responsável e contato definidos.
- Declarações corretas de segurança de dados, público e classificação de conteúdo.
- Teste interno; teste fechado quando aplicável à conta.
- Solicitação de acesso à produção e revisão da loja.
- Definição comercial e cobrança, se desejada, em implementação posterior; não há pagamentos nesta versão.

Na consulta de 14/10/2026, o requisito geral para novos apps Android é target API 36. Para contas pessoais novas abrangidas pela regra, há teste fechado com pelo menos 12 participantes por 14 dias contínuos. Verifique as exigências na submissão.

## Fontes oficiais consultadas
- https://developer.android.com/build/releases/agp-8-13-0-release-notes
- https://developer.android.com/develop/ui/views/layout/webapps/webview
- https://support.google.com/googleplay/android-developer/answer/11926878
- https://support.google.com/googleplay/android-developer/answer/14151465

A aprovação depende da análise da Google Play, e não é garantida pela estrutura do projeto.
