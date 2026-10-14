"use strict";
const api=window.IPIT, bridge=window.IPITAndroid, key="ipit-professor-v1";
const status=document.querySelector("#status");
function inform(text){status.textContent=text;}
let state=api.blank();
try{const saved=bridge?bridge.load():localStorage.getItem(key);if(saved)state=api.clean(JSON.parse(saved));}catch(e){inform("Não foi possível ler registros. Nenhum dado foi sobrescrito. Exporte o armazenamento antes de salvar novamente.");}
function save(){try{const raw=JSON.stringify(state);if(bridge){if(!bridge.save(raw))throw Error();}else localStorage.setItem(key,raw);inform("Registro salvo neste dispositivo.");return true;}catch(e){inform("Não foi possível salvar. Exporte uma cópia para preservar seu trabalho.");return false;}}
function el(tag,text){const node=document.createElement(tag);if(text!==undefined)node.textContent=text;return node;}
function showTab(id){document.querySelectorAll("main>section").forEach(s=>s.hidden=s.id!==id);document.querySelectorAll("[data-tab]").forEach(b=>b.setAttribute("aria-pressed",String(b.dataset.tab===id)));window.scrollTo(0,0);}
document.querySelectorAll("[data-tab]").forEach(b=>b.addEventListener("click",()=>showTab(b.dataset.tab)));
window.ipitBack=()=>{if(!document.querySelector("#plano").hidden)return false;showTab("plano");return true;};
const planForm=document.querySelector("#plan-form");
function fillPlan(){for(const field of api.fields)planForm.elements[field].value=state.plan[field];}
function capturePlan(){for(const field of api.fields)state.plan[field]=planForm.elements[field].value.slice(0,4000);}
planForm.addEventListener("submit",e=>{e.preventDefault();capturePlan();save();});
function updateProgress(){const count=Object.values(state.records).filter(x=>x.done).length;document.querySelector("#progress").value=count;document.querySelector("#progress-label").textContent=count+" de 5 momentos marcados como concluídos.";}
function renderStages(){const list=document.querySelector("#stage-list");list.replaceChildren();for(const s of api.stages){const card=el("article"),heading=el("h3",s.title+" · "+s.minutes+" min"),label=el("label"),check=el("input");check.type="checkbox";check.checked=state.records[s.id].done;check.addEventListener("change",()=>{state.records[s.id].done=check.checked;save();updateProgress();});label.append(check,document.createTextNode("Marcar etapa concluída"));const notesLabel=el("label","Evidência, teste ou decisão observada"),notes=el("textarea");notes.maxLength=4000;notes.value=state.records[s.id].note;notes.addEventListener("change",()=>{state.records[s.id].note=notes.value;save();});notesLabel.append(notes);card.append(heading,el("p",s.question),el("p",s.evidence),label,notesLabel);list.append(card);}updateProgress();}
const guideSelect=document.querySelector("#guide-stage");
for(const s of api.stages){const option=el("option",s.title);option.value=s.id;guideSelect.append(option);}
document.querySelectorAll("[data-guide]").forEach(b=>b.addEventListener("click",()=>{capturePlan();const log=document.querySelector("#conversation");const message=el("div",api.guide(state,guideSelect.value,b.dataset.guide));message.className="message";log.append(message);while(log.children.length>10)log.firstChild.remove();message.scrollIntoView({block:"nearest"});}));
document.querySelector("#clear-chat").addEventListener("click",()=>document.querySelector("#conversation").replaceChildren());
const levels=["Não observado","Inicial","Em desenvolvimento","Consistente"];
const descriptors=[
["Problema ou público pouco delimitado; sem evidência.","Problema delimitado; evidência parcialmente relacionada.","Problema e público claros, evidência pertinente e hipóteses identificadas."],
["Solução pouco relacionada; recurso sem justificativa.","Proposta relacionada; fluxo ou tecnologia precisam de explicação.","Solução coerente, fluxo compreensível e escolha tecnológica justificada."],
["Teste ausente ou sem registro.","Teste registrado, com revisão pouco explicada.","Tarefa e resultado registrados; ajuste coerente ou manutenção justificada."],
["Trabalho concentrado e decisões difíceis de explicar.","Divisão de tarefas e explicação parcial; participação desigual.","Contribuições distribuídas, revisão conjunta e comunicação clara."]
];
for(const [i,c]of api.criteria.entries()){const label=el("label",c),select=el("select");select.name="criterion"+i;for(const level of levels)select.append(el("option",level));label.append(select);document.querySelector("#criteria-inputs").append(label);const detail=el("div");detail.append(el("h3",c));descriptors[i].forEach((d,j)=>detail.append(el("p",levels[j+1]+": "+d)));document.querySelector("#rubric").append(detail);}
const evalForm=document.querySelector("#evaluation-form");
evalForm.addEventListener("submit",e=>{e.preventDefault();if(state.evaluations.length>=50){inform("Limite de 50 registros. Exporte antes de iniciar outro plano.");return;}state.evaluations.push({code:evalForm.elements.code.value.trim().slice(0,80),levels:api.criteria.map((_,i)=>evalForm.elements["criterion"+i].value),note:evalForm.elements.note.value.slice(0,4000)});save();evalForm.reset();renderEvaluations();});
function renderEvaluations(){const list=document.querySelector("#evaluations");list.replaceChildren();state.evaluations.forEach((r,i)=>{const card=el("article");card.append(el("h3",r.code));api.criteria.forEach((c,j)=>card.append(el("p",c+": "+r.levels[j])));card.append(el("p","Evidência e feedback: "+r.note));const remove=el("button","Excluir este registro");remove.className="secondary";remove.addEventListener("click",()=>{if(confirm("Excluir a avaliação desta equipe?")){state.evaluations.splice(i,1);save();renderEvaluations();}});card.append(remove);list.append(card);});}
function download(text,name){const url=URL.createObjectURL(new Blob([text],{type:"text/plain;charset=utf-8"}));const a=el("a");a.href=url;a.download=name;a.click();setTimeout(()=>URL.revokeObjectURL(url),1000);}
async function share(text,name){try{if(bridge){bridge.share(text);return;}if(navigator.share){await navigator.share({title:"IPIT Professor",text});return;}download(text,name);}catch(e){if(e.name!=="AbortError")inform("Compartilhamento indisponível; tente exportar no navegador.");}}
document.querySelector("#share-report").addEventListener("click",()=>{capturePlan();share(api.report(state),"ipit-relatorio.txt");});
document.querySelector("#share-backup").addEventListener("click",()=>{capturePlan();share(JSON.stringify(state,null,2),"ipit-backup.json");});
document.querySelector("#restore").addEventListener("click",()=>{try{const candidate=api.clean(JSON.parse(document.querySelector("#import-text").value));if(!confirm("Substituir os registros atuais pelo backup? Exporte antes se precisar preservá-los."))return;state=candidate;save();fillPlan();renderStages();renderEvaluations();document.querySelector("#import-text").value="";}catch(e){inform("Backup inválido: "+e.message);}});
document.querySelector("#erase").addEventListener("click",()=>{if(!confirm("Excluir plano, evidências e avaliações deste dispositivo?"))return;try{if(bridge){if(!bridge.erase())throw Error();}else localStorage.removeItem(key);state=api.blank();fillPlan();renderStages();renderEvaluations();document.querySelector("#conversation").replaceChildren();document.querySelector("#import-text").value="";inform("Todos os registros locais foram excluídos.");}catch(e){inform("Não foi possível excluir os registros.");}});
fillPlan();renderStages();renderEvaluations();

let remote=false,history=[],pending=null,epoch=0;
const mode=document.querySelector("#agent-mode"),sendButton=document.querySelector("#send-chat");
function bubble(role,text){const node=el("div",(role==="user"?"Você: ":"Agente: ")+text);node.className="message";document.querySelector("#conversation").append(node);node.scrollIntoView?.({block:"nearest"});}
function resetRemote(){epoch++;remote=false;pending=null;history=[];sendButton.disabled=false;bridge?.disconnectApi?.();document.querySelector("#api-key").value="";mode.textContent="Modo local: orientação por regras do IPIT, no dispositivo, sem envio externo."; }
document.querySelector("#api-form").addEventListener("submit",e=>{
 e.preventDefault();if(!bridge?.configureApi){inform("A conexão por API está disponível somente no aplicativo Android.");return;}
 if(!document.querySelector("#api-consent").checked)return;
 const endpoint=document.querySelector("#api-endpoint").value,model=document.querySelector("#api-model").value.trim(),secret=document.querySelector("#api-key").value.trim();
 const result=JSON.parse(bridge.configureApi(endpoint,model,secret));document.querySelector("#api-key").value="";
 if(!result.ok){inform(result.error||"Configuração inválida.");return;}
 epoch++;remote=true;pending=null;history=[];sendButton.disabled=false;
 mode.textContent="API configurada nesta sessão: "+model+". A conexão será testada na primeira mensagem. Perguntas e contexto do plano serão enviados ao provedor.";
 inform("Chave removida do formulário e mantida apenas na memória nativa.");
});
document.querySelector("#disconnect-api").addEventListener("click",resetRemote);
document.querySelector("#chat-form").addEventListener("submit",e=>{
 e.preventDefault();if(pending)return;capturePlan();
 const input=document.querySelector("#chat-input"),message=input.value.trim();if(!message)return;
 const topic=api.classify(message);bubble("user",message);input.value="";
 if(!remote||topic==="fora"){bubble("assistant",api.localReply(state,guideSelect.value,message));return;}
 const id=String(epoch)+"-"+Date.now();pending=id;sendButton.disabled=true;
 history.push({role:"user",content:message});history=history.slice(-6);
 bridge.chatApi(id,JSON.stringify(history),api.contextForApi(state,guideSelect.value));
 inform("Consultando o provedor. Aguarde até 45 segundos.");
});
window.ipitApiResult=(id,text,error)=>{
 if(id!==pending)return;pending=null;sendButton.disabled=false;
 if(error){if(history.at(-1)?.role==="user")history.pop();bubble("assistant",error+" Você pode desconectar e continuar no modo local.");inform("A consulta não foi concluída.");return;}
 history.push({role:"assistant",content:text});history=history.slice(-6);
 bubble("assistant",text+"\n\nSugestão de IA: revise fatos, fontes, código e adequação pedagógica antes de usar.");
 inform("Resposta recebida. Revise antes de incorporar.");
};
if(!bridge?.configureApi){document.querySelector("#api-form").querySelectorAll("input,select,button").forEach(x=>x.disabled=true);}
document.querySelector("#clear-chat").addEventListener("click",()=>{history=[];epoch++;pending=null;sendButton.disabled=false;});
document.querySelector("#erase").addEventListener("click",()=>{if(status.textContent==="Todos os registros locais foram excluídos.")resetRemote();});
