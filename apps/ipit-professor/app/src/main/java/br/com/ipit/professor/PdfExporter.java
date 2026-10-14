package br.com.ipit.professor;
import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import java.io.*;
import java.util.*;

public final class PdfExporter {
 private PdfExporter(){}
 public static File create(Context context,String report) throws IOException {
  if(report==null || report.trim().isEmpty() || report.length()>200000)throw new IOException("Relatório inválido");
  File directory=new File(context.getCacheDir(),"pdf-exports");
  if(!directory.exists()&&!directory.mkdirs())throw new IOException("Falha ao criar pasta");
  // Arquivo distinto por exportação: não altera um PDF já compartilhado.
  File file=new File(directory,"ipit-relatorio-"+UUID.randomUUID()+".pdf");
  Paint body=new Paint(Paint.ANTI_ALIAS_FLAG);body.setColor(Color.rgb(24,59,66));body.setTextSize(12);body.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
  Paint title=new Paint(body);title.setTextSize(18);title.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
  Paint small=new Paint(body);small.setTextSize(9);
  List<String> lines=wrap(report.replace("\t","    "),body,515);
  PdfDocument document=new PdfDocument();
  try{
   int index=0,pageNumber=0;
   do {
    PdfDocument.Page page=document.startPage(new PdfDocument.PageInfo.Builder(595,842,++pageNumber).create());
    try{
    Canvas canvas=page.getCanvas();
    canvas.drawColor(Color.WHITE);
    canvas.drawText("IPIT Professor",40,48,title);
    canvas.drawText("Planejamento, evidências e avaliação docente",40,66,small);
    Paint line=new Paint();line.setColor(Color.rgb(23,96,79));line.setStrokeWidth(2);canvas.drawLine(40,77,555,77,line);
    float y=100;
    while(index<lines.size() && y<=770){canvas.drawText(lines.get(index++),40,y,body);y+=17;}
    canvas.drawText("Sandra Maria Pereira · Conteúdo educacional CC BY 4.0 · Página "+pageNumber,40,808,small);
    }finally{document.finishPage(page);}
   }while(index<lines.size());
   try(OutputStream out=new FileOutputStream(file)){document.writeTo(out);}
  }catch(IOException | RuntimeException e){file.delete();throw e;}
  finally{document.close();}
  return file;
 }
 static List<String> wrap(String text,Paint paint,float width){
  List<String> result=new ArrayList<>();
  for(String paragraph:text.split("\\n",-1)){
   String rest=paragraph;
   if(rest.isEmpty()){result.add("");continue;}
   while(!rest.isEmpty()){
    int count=paint.breakText(rest,true,width,null);
    if(count<=0)count=Character.charCount(rest.codePointAt(0));
    if(count<rest.length()){
     if(count>0 && Character.isHighSurrogate(rest.charAt(count-1)))count--;
     int space=rest.lastIndexOf(' ',count);
     if(space>0)count=space;
    }
    if(count<=0)count=Character.charCount(rest.codePointAt(0));
    result.add(rest.substring(0,count));rest=rest.substring(count).replaceFirst("^ +","");
   }
  }
  return result;
 }
}
