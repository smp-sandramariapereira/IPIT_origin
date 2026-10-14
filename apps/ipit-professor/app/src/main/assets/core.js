/* IPIT Professor — regras locais independentes da interface. */
(function(root){
"use strict";
const fields=["objective","subject","group","challenge","resources","duration","supports","technology","aiUse"];
const stages=[
{id:"investigar",title:"🔎 Investigar",minutes:30,question:"Quem enfrenta o problema e que evidência temos?",evidence:"Público, problema, fonte da evidência e hipóteses.",tip:"Não aceite uma resposta de IA como evidência de uma necessidade real."},
{id:"idear",title:"💡 Idear",minutes:30,question:"Quais duas alternativas foram comparadas e por que escolher uma?",evidence:"Alternativas, escolha e justificativa.",tip:"Escolha a tecnologia depois de compreender a necessidade."},
{id:"prototipar",title:"🛠️ Prototipar",minutes:55,question:"Qual entrada, regra e saída mostram o fluxo principal?",evidence:"Protótipo e decisão tecnológica explicada.",tip:"Reduza o escopo; papel, planilha, slides ou código conforme objetivos."},
{id:"testar",title:"🧪 Testar e melhorar",minutes:35,question:"O colega consegue realizar a tarefa? O que precisa mudar?",evidence:"Tarefa, esperado, observado, ajuste e limite.",tip:"O teste com colega é inicial e não comprova aceitação pelo público real."},
{id:"refletir",title:"💬 Apresentar e refletir",minutes:45,question:"Que decisão mudou e o que cada estudante aprendeu?",evidence:"Demonstração, limites e reflexão individual.",tip:"Até seis equipes: quatro minutos de apresentação e dois de feedback."}
];
const criteria=["Compreensão do problema","Coerência da solução e decisão tecnológica","Teste e melhoria","Colaboração e comunicação"];
function blank(){return {version:1,plan:Object.fromEntries(fields.map(k=>[k,k==="duration"?"240":""])),records:Object.fromEntries(stages.map(s=>[s.id,{done:false,note:""}])),evaluations:[]};}
function clean(raw){
 if(!raw || raw.version!==1 || !raw.plan || !raw.records) throw Error("Backup incompatível. Use um registro IPIT versão 1.");
 const out=blank();
 for(const k of fields) out.plan[k]=typeof raw.plan[k]==="string"?raw.plan[k].slice(0,4000):out.plan[k];
 for(const s of stages){const r=raw.records[s.id];if(r){out.records[s.id]={done:r.done===true,note:typeof r.note==="string"?r.note.slice(0,4000):""};}}
 out.evaluations=Array.isArray(raw.evaluations)?raw.evaluations.slice(0,50).filter(x=>x&&typeof x.code==="string"&&Array.isArray(x.levels)).map(x=>({code:x.code.slice(0,80),levels:criteria.map((_,i)=>["Não observado","Inicial","Em desenvolvimento","Consistente"].includes(x.levels[i])?x.levels[i]:"Não observado"),note:typeof x.note==="string"?x.note.slice(0,4000):""})):[];
 return out;
}
function guide(state,stageId,kind){
 const s=stages.find(x=>x.id===stageId)||stages[0];
 const context=state.plan.objective? "Objetivo curricular: "+state.plan.objective+". ":"Defina primeiro um objetivo curricular observável. ";
 const response=kind==="exemplo" ? "Exemplo fictício: para consultar empréstimos de materiais, represente disponibilidade por códigos e estados. Use dados simulados. Adapte o raciocínio ao seu desafio, sem copiar a solução." : kind==="dificuldade" ? "Reduza a tarefa ao menor passo observável. Distribua contribuições, retome a evidência e adapte o recurso disponível. "+s.tip : kind==="evidencia" ? "Registre: "+s.evidence+" Se usar IA, indique ferramenta, instrução, verificação e alteração humana." : s.tip;
 return context+"\n\n"+response+"\n\nPergunta para continuar: "+s.question;
}
function report(state){
 return "IPIT Professor — plano e evidências\n"+fields.map(k=>k+": "+state.plan[k]).join("\n")+"\n\n"+stages.map(s=>s.title+" — "+(state.records[s.id].done?"concluído":"em andamento")+"\n"+state.records[s.id].note).join("\n\n")+"\n\nAvaliação\n"+state.evaluations.map(x=>x.code+"\n"+criteria.map((c,i)=>c+": "+x.levels[i]).join("\n")+"\nFeedback: "+x.note).join("\n\n")+"\n\nAutora da metodologia: Sandra Maria Pereira. Registros requerem revisão docente.";
}
const api={fields,stages,criteria,blank,clean,guide,report};
if(typeof module!=="undefined"&&module.exports)module.exports=api;else root.IPIT=api;
})(typeof globalThis!=="undefined"?globalThis:this);
