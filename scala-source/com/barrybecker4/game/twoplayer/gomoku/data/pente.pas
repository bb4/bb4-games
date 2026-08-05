  program PENTE;
    const Look=1;    {the lookahead factor                      }
                      {Increases the time proportional to e^look }
                      {eg: .3 seconds on look=1;
                          1.5 seconds on look=2;
                          9.0  seconds on look=3;
                         80.  seconds on look=4 ...  }
          MaxX=77;
          MaxY=23;    {the maximum x and y dimensions of the grid}
          MaxNumWts=24;   {number of coefficients                }
          MaxLengthp1=7;
          BIGINT=3000;
          Win=410;          {greater than this is a winning score }
          MaxLineLength=12; {this is 2*MaxLength }
          BlankSymb= '_';
          PlayerSymb='X';
          CompSymb=  'O';
     type boardType=array[1..MaxY,1..MaxX] of char;
          ptr=^node;
          node=record
              row,col:byte;
              value:integer;
              moves:integer;
              player:boolean;
              next:ptr;
           end;
          lineType=string[MaxLineLength];
          vector=array[1..MaxNumWts] of integer;   {vector of weights}
          str=string[MaxLengthp1];
          patternType=array[1..MaxNumWts] of str;
      var fil:text;
          rt:ptr;
          NumWts,LineLength,Nx,Ny,NxNy,M:integer;    { global }
          ManVsComp,FirstTime:boolean; {MVC: if false then coomp plays itself}
          Pattern:patternType;

*****************************
procedure INITIALIZE(var wts:vector);
      var i,j:integer;
          p:string[10];
          c:char;
    begin
          assign(fil,'pente.dat');
          reset(fil);
          new(rt);
          for i:=1 to MaxNumWts*(M-3) do
            begin readln(fil,p); {write(p,'  ');}
                  readln(fil,p) {writeln(p)}
             end;
          for i:=1 to NumWts do
           begin
              Pattern[i]:='';
              while not EOLN(fil) do
               begin
                read(fil,c);
                Pattern[i]:=Pattern[i]+c;
               end;
              readln(fil);
              readln(fil,wts[i]);
           end;
          close(fil);
          wts[4*M-3]:=  BIGINT;
          wts[4*M-2]:= -BIGINT;
          for i:=1 to NumWts do
            writeln(Pattern[i]:25,wts[i]:10);
          repeat until keypressed;
     end;
(******************************)
procedure SETUP(var board:boardType; var rootnode:node);
      var i,j,p:integer;
    begin
          FirstTime:=TRUE;
          for i:=1 to Nx do
           for j:=1 to Ny do
            board[j,i]:=BlankSymb;
          textmode(3);{write(^g);}
          rootnode.row:=M;
          rootnode.col:=M;
          rootnode.value:=0;
          rootnode.moves:=0;
          rootnode.next:=nil;
          writeln('nodevals:',rootnode.row,rootnode.moves);
          TextColor(8);
          for i:=1 to Nx+2 do
           begin
              gotoxy(i,1);    write('-');
              gotoxy(i,Ny+2); write('-');
           end;
          for i:=1 to Ny do
           begin
              gotoxy(1,1+i);    write('|');
              gotoxy(Nx+2,1+i); write('|');
           end;
    end;
(**********************)
 {function NOMOVES(var board:boardType ):boolean;
      var r,c:integer;
          done:boolean;
    begin
          done:=FALSE; r:=0;
          repeat
           r:=r+1; c:=0;
           repeat
             c:=c+1;
             if (board[r,c]=BlankSymb) then done:=TRUE;
           until done or (c>=Nx);
          until done or (r>=Ny);
          if not done then NOMOVES:=TRUE
          else NOMOVES:=FALSE;
     end;
  }
(********************************)
function FIND_DIFF(var line:lineType; position:integer; wt:vector):integer;
         {return the difference  newscore-oldscore }
     var i,j:integer;
         temp:char;
         old,new,p:integer;
   begin
         old:=0; new:=0;
         {write(line,'  pos(',position:2,')');}
         temp:=line[position];
         line[position]:=BlankSymb;
         for i:=1 to NumWts do
          begin
           p:=POS(Pattern[i],line);
           {writeln('Pattern:',Pattern[i],' line:',line:12,' position:',p);}
           if (p>0) then  {the pattern is there}
             begin {write(Pattern[i]:5,'pos',p:3);} old:=old+wt[i]; end;
          end;
         line[position]:=temp;
         for i:=1 to NumWts do
          begin
             p:=POS(Pattern[i],line);
             if (p>0) then
               begin
                 {gotoXY(56,6);
                 textcolor(12);
                 write(Pattern[i]:5,'pos',p:2,' wt ',wt[i]:2);
                 write(^G); gotoXY(59,6);textcolor(8);
                 write(Pattern[i]:5,'pos',p:2,' wt ',wt[i]:2);}
                 new:=new+wt[i];
               end;
          end;
         {writeln('  diff:',new-old);}
         FIND_DIFF:=new-old;
    end;
(******************************)
function WORTH(var board:boardType; last:ptr;
         wts:vector; vn:integer):integer;
         {returns the last value modified by the new move }
         {a positive worth means the player has the advantage}
     var position,i,j:integer;
         line:linetype;
         weight,row,col,stop,old,new,value:integer;
         ct,ctr,ctc,stopr,stopc:integer;
(*---------------------------*)
procedure DEBUG(symb:char);
    begin
          gotoXY(55,2); write(symb,line:6);
          gotoXY(55,3); write('pos ',position:2,' val: ',value:2);
          repeat until keypressed;
     end;
(*---------------------------*)
   begin
         row:=last^.row;
         col:=last^.col;
         ctc:=col-LineLength+1;   (* - *)
         if (ctc<1) then ctc:=1;
         stopc:=col+LineLength-1;
         if (stopc>Nx) then stopc:=Nx;
         repeat
           ctc:=ctc+1;
         until (board[row,ctc]<>BlankSymb) or (ctc>=col);
         ctc:=ctc-1;
         position:=col-ctc+1;
         line:='';
         for i:=ctc to stopc do
           line:=line+board[row,i];
         value:=FIND_DIFF(line,position,wts);
         DEBUG('-');

         ctr:=row-LineLength+1;   (* | *)
         if (ctr<1) then ctr:=1;
         stopr:=row+LineLength-1;
         if (stopr>Ny) then stopr:=Ny;
         repeat
           ctr:=ctr+1;
         until (board[ctr,col]<>BlankSymb) or (ctr>=row);
         ctr:=ctr-1;
         position:=row-ctr+1;
         line:='';
         for i:=ctr to stopr do
           line:=line+board[i,col];
         value:=value+FIND_DIFF(line,position,wts);
         {DEBUG('|');}

         ctc:=col-LineLength+1;   (* \ *)
         ctr:=row-LineLength+1;
         if (ctc<1) then
          begin  ctr:=ctr+1-ctc; ctc:=1; end;
         if (ctr<1) then
          begin  ctc:=ctc+1-ctr; ctr:=1; end;
         stopc:=col+LineLength-1;
         stopr:=row+LineLength-1;
         if (stopc>Nx) then
          begin stopr:=stopr+Nx-stopc; stopc:=Nx; end;
         if (stopr>Ny) then
          begin stopc:=stopc+Ny-stopr; stopr:=Ny; end;
         repeat
           ctr:=ctr+1; ctc:=ctc+1;
         until (board[ctr,ctc]<>BlankSymb) or (ctr>=row);
         ctr:=ctr-1; ctc:=ctc-1;
         position:=row-ctr+1;
         line:='';
         for i:=ctr to stopr do
           line:=line+board[i,ctc+i-ctr];
         value:=value+FIND_DIFF(line,position,wts);
         {DEBUG('\');}

         ctc:=col-LineLength+1;  (* / *)
         ctr:=row+LineLength-1;
         if (ctc<1) then
          begin ctr:=ctr+ctc-1; ctc:=1; end;
         if (ctr>Ny) then
          begin ctc:=ctc-Ny+ctr; ctr:=Ny; end;
         stopc:=col+LineLength-1;
         stopr:=row-LineLength+1;
         if (stopc>Nx) then
          begin stopr:=stopr-Nx+stopc; stopc:=Nx; end;
         if (stopr<1) then
          begin stopc:=stopc+stopr-1; stopr:=1; end;
         repeat
          ctr:=ctr-1; ctc:=ctc+1;
         until (board[ctr,ctc]<>BlankSymb) or (ctc>=col);
         ctr:=ctr+1; ctc:=ctc-1;
         position:=col-ctc+1;
         line:='';
         for i:=ctc to stopc do
           line:=line+board[ctr-i+ctc,i];
         value:=value+FIND_DIFF(line,position,wts);
         {DEBUG('/');}
         WORTH:=last^.value+(1-2*vn)*value;
    end;
(*********************)
procedure PRINT(list:ptr);
    begin writeln('the list of moves:');
          while list<>nil do
           begin
              writeln('-- ',list^.row:3,list^.col:3,
               list^.value:5,list^.player:5,list^.moves:3);
              list:=list^.next;
           end;
     end;
(******************************)
 function MOVES(board:boardType; last:ptr; playermove:boolean):ptr;
          {generates a linked list of possible moves}
      var list:ptr;
          test:boolean;
          i,j,ip1,jp1,im1,jm1:integer;
          p:ptr;

    begin
          list:=nil;
          for i:=1 to Nx do      {col}
           for j:=1 to Ny do    {row}
            if (board[j,i]=BlankSymb) then
             begin
               if (i-1)<1  then im1:=1   else im1:=i-1;
               if (i+1)>Nx then ip1:=Nx else ip1:=i+1;
               if (j-1)<1  then jm1:=1   else jm1:=j-1;
               if (j+1)>Ny then jp1:=Ny else jp1:=j+1;
               test:=(board[jm1,i]=BlankSymb) and (board[jm1,im1]=BlankSymb) and
                    (board[jm1,ip1]=BlankSymb) and (board[jp1,i]=BlankSymb)
                    and (board[jp1,im1]=BlankSymb) and (board[jp1,ip1]=BlankSymb)
                    and (board[j,im1]=BlankSymb) and (board[j,ip1]=BlankSymb);
               if not test then
                 begin
                     new(p);
                     p^.row:=j;
                     p^.col:=i;
                     p^.value:=last^.value;
                     p^.moves:=last^.moves+1;
                     p^.player:=playermove;
                     {writeln('newmove:',p^.row:4,p^.col:4,p^.value:6,p^.player:6);}
                     p^.next:=list;
                     list:=p;
                  end;
              end;
          MOVES:=list;
     end;
(*****************************)
procedure KILL(var p:ptr);
      var q:ptr;
    begin
          while (p<>nil) do
           begin
              q:=p;
              p:=p^.next;
              dispose(q);
           end;
     end;
(*************************)
function SEARCH(var board:boardType; last:ptr; wts:vector; vn,depth:integer;
                var min:integer; passmin:integer):integer;
      var list,prev:ptr;
          newvalue,i:integer;
          player:boolean;
    begin
          player:=odd(depth{+vn});
          gotoXY(70,depth+16); writeln('depth',depth:3);
          last^.value:=WORTH(board,last,wts,vn);
          if (depth=Look) or (last^.moves=NxNy)
             or (abs(last^.value)>Win) then
              begin
                  {writeln('moves',last^.moves);}
                  if player then search:=last^.value
                  else search:= -last^.value;
              end
          else
           begin
              list:=MOVES(board,last,player);
              {gotoxy(50,24); writeln(MEMAVAIL);    print MEMORY}
              {PRINT(list);}
              while list<>nil do
               begin
                  prev:=list;
                  list:=list^.next;
                  prev^.next:=nil;
                  if prev^.player then
                   begin
                     board[prev^.row,prev^.col]:=PlayerSymb;
                     gotoXY(prev^.col+1,prev^.row+1);
                     TextColor(10); write('X');
                   end
                  else
                   begin
                      board[prev^.row,prev^.col]:=CompSymb;
                      gotoXY(prev^.col+1,prev^.row+1);
                      TextColor(15); write('O');
                   end;
                  passmin:=  -passmin;
                  newvalue:= -SEARCH(board,prev,wts,vn,depth+1,passmin,-min);
                  passmin:=  -passmin;
                  board[prev^.row,prev^.col]:=BlankSymb;
                  gotoXY(50,7+depth); textcolor(11);
                  writeln(depth,'player ',prev^.player,' val ',newvalue:2);
                  sound((depth+1)*100); delay(1000); nosound;
                  repeat until keypressed;
                  gotoXY(50,7+depth); textcolor(3);
                  writeln(depth,'player ',
                    prev^.player,' val ',newvalue:2,'   ');
                  gotoXY(prev^.col+1,prev^.row+1);
                  write(' ');
                  if newvalue>passmin then
                   begin
                      passmin:=newvalue;
                      gotoXY(60,20); write(passmin);
                      if (last^.next<>nil) then KILL(last^.next);
                      last^.next:=prev;
                   end
                  else
                   KILL(prev);
                  {this does a-b pruning }
                  if (passmin>=min) then KILL(list);
               end;
              search:=passmin;
           end;
     end;
(******************************)
procedure FIRST_MOVE(var board:boardType; var root:ptr;
                     vn:integer; wts:vector);
     var delta,r,c:byte;
   begin
         if (LineLength>4) then delta:=2
         else delta:=1;
         c:=RANDOM(Nx-2*delta)+delta+1;
         r:=RANDOM(Ny-2*delta)+delta+1;
         if vn=0 then
          board[r,c]:=CompSymb
         else
          board[r,c]:=PlayerSymb;
         root^.col:=c;
         root^.row:=r;
         root^.value:=WORTH(board,root,wts,vn);
         root^.moves:=1;
         {writeln('worth after 1st move:',root^.value);}
         root^.player:=FALSE;
    end;
(******************************)
function COMPUTER(var board:boardType; var root:ptr; wts:vector;
         version:integer; symbol:char):boolean;
     var val,min,max,sign:integer;
         nomore:boolean;
         p:ptr;
   begin
         sign:=(1-version*2);
         max:=  2*BIGINT;
         min:= -2*BIGINT;
         computer:=FALSE;
         gotoXY(47,14);
         if root=nil then
          begin writeln('null before first move. ft:',FirstTime:2);
                repeat until keypressed;
           end;
         if FirstTime then
          begin
             FIRST_MOVE(board,root,version,wts);
             FirstTime:=FALSE;
          end
         else
          begin
             if root=nil then writeln('null before search');
             val:=SEARCH(board,root,wts,version,0,max,min);
             TextColor(14);
             if root=nil then writeln(^g,'the root is null!');
             p:=root^.next;
             if (p=nil) then writeln('error')
             else
              begin
               dispose(p^.next);
               p^.next:=nil;
              end;
             dispose(root);
             root:=p;
             gotoxy(31,23);
             {write('version',version:3,'value: ',val:5,
                ' root^.value ',root^.value,win:5);}
             if (version=0) then
              board[root^.row,root^.col]:=CompSymb
             else board[root^.row,root^.col]:=PlayerSymb;
             nomore:=(root^.moves=NxNy);
             {gotoxy(50,22); write('nm',nomore:6);}
             if nomore or (root^.value< -Win) then
              begin
                 computer:=TRUE;
                 gotoxy(45,23); TextColor(38);
                 if (root^.value<-Win) then
                   writeln(^g,' Computer (',symbol,') has Won! ')
                 else
                   writeln('no more(',nomore,') moves.', ^g,' A tie. ')
              end
           end;
         gotoxy(root^.col+1,root^.row+1);
         TextColor(14);
         write(symbol);
    end;
(**********************************************)
 function PLAYER(var board:boardType; var root:ptr; wts:vector):boolean;
      var row,col:integer;
          key:char;
          done:boolean;
    begin
          player:=FALSE;
          row:=root^.row;
          col:=root^.col;
          done:=FALSE;
          repeat
            gotoxy(col+1,row+1);
            repeat until keypressed;
            read(kbd,key);
            case key of
             'i': begin
                    row:=row-1;
                    if (row<1) then row:=1;
                  end;
             'k': begin
                    row:=row+1;
                    if (row>Ny) then row:=Ny;
                  end;
             'j': begin
                    col:=col-1;
                    if col<1 then col:=1;
                  end;
             'l': begin
                    col:=col+1;
                    if col>Nx then col:=Nx;
                  end;
             'x',
             'X':if board[row,col]=BlankSymb then
                      begin
                         TextColor(2);
                         write('X');
                         TextColor(14);
                         done:=TRUE;
                      end;
            end;
          until done;
          board[row,col]:=PlayerSymb;
          root^.row:=row;
          root^.col:=col;
          root^.moves:=root^.moves+1;
          if WORTH(board,root,wts,0)>Win then
           begin
             writeln(^g,' Player has won!');
             player:=TRUE;
           end
          else if (root^.moves=NxNy) then
           begin
              player:=TRUE;
              writeln(^g,' A tie ');
           end;
          {writeln('player pos:',root^.row,root^.col);}
          if FirstTime then
           begin root^.value:=0; FirstTime:=FALSE; end;
          root^.player:=TRUE;
     end;
(******************************)
procedure MAN_COMP;
      var q:char;
          board:boardType;
          count:integer;
          winner:str;
          wts:vector;
          done:boolean;
    begin
          done:=FALSE;
          TextColor(14);
          write('Do you want to go first?(y/n)');readln(q);
          INITIALIZE(wts);
          SETUP(board,rt^);
          count:=0;
          if upcase(q)='Y' then
           begin done:=PLAYER(board,rt,wts); FirstTime:=FALSE; end;
          while not done do
           begin
              done:=COMPUTER(board,rt,wts,0,'O');
              {gotoxy(50,24); writeln(MEMAVAIL);}
              count:=count+1;
              if not done then  { if done then c or p has placed final move}
                 done:=PLAYER(board,rt,wts);
              count:=count+1;
           end;
          repeat until keypressed;
          writeln('There were count ',count,' turns.');
          writeln('Thanks for playing.');
          {clrscr;}
     end;
(*******************************)
function COMPUTER_COMPARE(var board:boardType;
          var oldwts,newwts:vector):real;
      var q:char;
          count:integer;
          done,newWon:boolean;
    begin

          SETUP(board,rt^);
          count:=0;
          done:=FALSE;
          newWon:=FALSE;
          if rt=nil then writeln(^g,^g,'null!');
          repeat until keypressed;
          done:=COMPUTER(board,rt,oldwts,0,'O');
          while not done do
           begin
              done:=COMPUTER(board,rt,newwts,1,'#');
              if done then newWon:=TRUE;
              count:=count+1;
              if not done then {if done the final move was placed}
               done:=COMPUTER(board,rt,oldwts,0,'O');
              count:=count+1;
           end;
          repeat until keypressed;
          writeln('there were ',count,' turns');
          if newWon then
            COMPUTER_COMPARE:=M*10./count
          else COMPUTER_COMPARE:= -M*10./count;  {strength of the win}
          gotoXY(50,24); writeln('str win:',M*10./count:5:2);
          {KILL(root); root:=nil; }
          {clrscr; }
     end;
(********************************)
procedure OPTIMIZE(var board:boardType; var oldwts:vector);
     var i,j,i2,delta:integer;
         grad,newwts:vector;
         fvect:array [1..MaxNumWts] of real;
         w1,w2,t:real;
   begin
         delta:=2;
         repeat
           t:=0;
           for i:=NumWts div 2 downto 1 do
            begin
               i2:=2*i;
               for j:=NumWts downto 1 do
                newwts[j]:=oldwts[j];
               newwts[i2]:=oldwts[i2]+delta;
               newwts[i2+1]:=oldwts[i2+1]-delta;
               w1:=COMPUTER_COMPARE(board,oldwts,newwts);
               w2:=COMPUTER_COMPARE(board,newwts,oldwts);
               fvect[i]:=w1+w2;
               write(i,fvect[i]:6:2,' ');
               t:=t+fvect[i]*fvect[i];
            end;
           writeln('t is ',t:9:3);
           for i:=1 to NumWts div 2 do
            begin
               i2:=2*i;
               writeln('oldwts[',i2-1,']',oldwts[i2-1]:5,
                   'fvect',fvect[i2-1]:6:3);
               writeln('oldwts[',i2,']',oldwts[i2]:5,
                   'fvect',fvect[i2]:6:3);
               grad[i]:=round(10.*fvect[i]/(t+0.0001));
               newwts[i2-1]:=oldwts[i2-1]+grad[i];
               newwts[i2]:=oldwts[i2]-grad[i];
            end;
           if (t<11) then delta:=1;
         until (t<4);  {will continue until convergence }
         writeln('The optimized weights are :');
         for i:=1 to NumWts do  writeln('wt[',i,']=',newwts[i]);
         repeat until keypressed;
    end;
(********************************)
procedure COMP_COMP;
      var newwts,oldwts:vector;
          board:boardType;
          i,i2:integer;
          q:char;
          w1,w2:real;
    begin
          INITIALIZE(oldwts);
          writeln('Do you want to test new wts or run an optimization (T/O)?');
          readln(q);
          if upcase(q)='O' then OPTIMIZE(board,oldwts)
          else
           begin
             writeln('Enter the weights for the new comp');
             for i:=1 to NumWts div 2 do
              begin
                i2:=2*i-1;
                if (i2<>4*M-3) then
                 begin
                    write('Pattern ',Pattern[i2],' has wt: ',oldwts[i2]);
                    write(' New wt?: ');
                    readln(newwts[i2]);
                 end
                else newwts[i2]:=oldwts[i2];
                newwts[i2+1]:= -newwts[i2];
              end;
             w1:=COMPUTER_COMPARE(board,oldwts,newwts);
             writeln('oldwts went first. They won by ',w1:8:3);
             repeat until keypressed;
             w2:=COMPUTER_COMPARE(board,newwts,oldwts);
             writeln('newwts went first. They won by ',w2:8:3);
             writeln('The average win was ',(w1+w2)/2.:8:3,' for newwts');
             writeln(' a negative score indicates a loss.');
             repeat until keypressed;
            end;
       end;
(*******************************)
   begin
         INSTRUCT(manvscomp);
         if manvscomp then MAN_COMP
         else COMP_COMP;
    end.

