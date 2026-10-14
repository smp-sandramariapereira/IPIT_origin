# Agente conversacional 0.2.0
## Modo local
Mensagens livres são classificadas em planejamento, percurso, tecnologias, IA e avaliação. As respostas são trechos e perguntas predefinidas baseadas no IPIT, com objetivo curricular como contexto. Não existe modelo de linguagem no dispositivo; o classificador não compreende toda pergunta e não deve ser apresentado como análise semântica robusta. Perguntas sem categoria reconhecida recebem uma mensagem de limite.

## API opcional no Android
Configurar provedor, nome exato do modelo, chave própria e consentimento. Os endpoints permitidos estão em TeacherApi.java e usam HTTPS sem redirecionamento. A chave fica em memória nativa, é removida do formulário e não é salva em SharedPreferences, localStorage ou backup. Desconectar ou destruir a Activity remove a referência à configuração. Strings na memória gerenciada não oferecem garantia de zeroização física.
A primeira mensagem verifica a conexão de fato; configurar não comprova credenciais ou modelo válidos. Não há chamada cobrada de teste ao configurar.
Envio: contexto de objetivo, componente, desafio, recursos, tempo, apoios, tecnologia e IA, mais até seis mensagens. Código da turma, avaliações e evidências não são enviados automaticamente. Texto digitado pelo professor pode conter informações indevidas: revise antes de enviar.
Sem API no navegador de demonstração. A WebView não faz requisições externas; a chamada HTTPS é feita na camada Android. Não existe backend compartilhado.

## Limites
A triagem local bloqueia envio de perguntas sem categoria conhecida. A instrução do modelo restringe ao IPIT, mas não é uma barreira infalível: perguntas com palavras do domínio ou instruções adversariais podem escapar. Todas as sugestões precisam de revisão docente. Não há navegação, execução de código ou certificação.
No máximo uma consulta simultânea e intervalo de três segundos; timeouts de conexão e leitura, resposta limitada. Erros não expõem respostas brutas do provedor nem chaves.
Provedores podem cobrar e reter conteúdo conforme seus termos. Confirmar compatibilidade do modelo com Chat Completions antes do uso. Nenhuma chamada paga foi executada nesta entrega.

## Próxima validação
Compilar Android, testar com servidor simulado e com modelo autorizado, avaliar perguntas fora do escopo e tentativas de mudar instruções. Antes da loja, atualizar política pública e declarações de segurança de dados conforme o comportamento real.

Modelos precisam aceitar o parâmetro max_tokens (limite de 1200 nesta implementação). Alguns modelos exigem parâmetros diferentes; erros de compatibilidade são mostrados sem expor dados brutos.

Referências de protocolo consultadas em 14/10/2026:
- https://developers.openai.com/api/reference/resources/chat
- https://console.groq.com/docs/api-reference
- https://docs.mistral.ai/api/endpoint/chat
- https://api-docs.deepseek.com/zh-cn/api_samples/chat_curl/
