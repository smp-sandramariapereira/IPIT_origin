import {createServer} from "node:http";
import {readFile} from "node:fs/promises";
const root=new URL("../app/src/main/assets/",import.meta.url);
const names=new Set(["index.html","styles.css","core.js","app.js"]);
createServer(async(req,res)=>{const pathname=new URL(req.url,"http://localhost").pathname;const name=pathname==="/"?"index.html":pathname.slice(1);if(!names.has(name)){res.writeHead(404).end();return;}try{const data=await readFile(new URL(name,root));res.writeHead(200,{"content-type":name.endsWith(".css")?"text/css":name.endsWith(".js")?"application/javascript":"text/html","cache-control":"no-store"}).end(data);}catch{res.writeHead(500).end();}}).listen(4180,"127.0.0.1",()=>console.log("IPIT Professor: http://127.0.0.1:4180"));
