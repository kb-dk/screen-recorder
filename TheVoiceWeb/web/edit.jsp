<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html><head>
    <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
    <script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script type="text/javascript">
        displayTime = function() {
            $.ajax({
                url: "time.do",
                cache: false
            }).done(function( html ) {
                        $("p#time").text(html);
                    });
        }

        $(document).ready(function() {
            setInterval(function(){displayTime();}, 1000);
            $("#vis").click(function() {
                $("#manual").toggle();
            });
            $.ajax({
                url: "manual.do",
                cache: false
            }).done(function (html) {
                        $("#manual").html(html);
                    });
        });
    </script>
    <link href="${pageContext.request.contextPath}/style.css" type="text/css" rel="stylesheet">
    <title>Edit job</title></head><body>
<c:if test="${not empty param.changed}" >
    <p color="red">Your job has now been changed.</p>
</c:if>

<form method=post action=change.do>
    <input type="hidden" name="file" value="${fileModel.current}"/>
    <input type="hidden" name="referer" value="edit" />
    <textarea rows="50" name="fileContent" cols="50">${fileModel.content}</textarea>
    <br/>
    <input type=submit name=submit value=Change>
</form>
<br/>
<p id="time">${fileModel.time}</p>

<a href="showjobs.do">Back to overview.</a>

<button id="vis">Vis/gem manual</button>
<div id="manual" style="display: none; width: 55%">
    <br/>Et "job" er et script der schedulerer en optagelse givet et starttidspunkt, et sluttidspunkt og nogle kommandoer ind imellem. Disse kommandoer er kort fortalt:<br/>

    <b>open</b><br/>
    Åbner et program
    <br/>
    <b>write</b><br/>
    skriver ting på skærmen for dig
    <br/>
    <b>type</b><br/>
    Trykker på enkelte knapper, som f.eks. ENTER og TAB
    <br/>
    <b>click x y</b><br/>
    Rykker musen til (x,y) på skærmen og trykker
    <br/>
    <b>paste</b><br/>
    Copy paster noget til skærmen. Nogle strenge er lidt tricky(tm).
    <br/>
    <b>every</b><br/>
    Udfører en af de allerede nævnte kommandoer hvert x'te sekund.
    <br/>
    Man specificerer hvornår man vil have kørt sin kommando med at skrive et tal før sin kommando. Dette tal indikerer hvor mange sekunder efter jobbet startede, at man vil have sin kommando kørt. Disse tal skal forekomme i stigende orden.
    <br/>
    <p>At bygge et job</p>
    <br/>
    Vi tager et par eksempler på hvordan man kan bygge sådan et job sammen. Lad os starte med at optage en youtube video.
    <br/>
<pre>
===== Start på eksempel 1 =====
start 18-52-00-30-04-2013
15 open firefox -private
30 write http://www.youtube.com/watch?v=AwUh1mDagIc
32 type Return
stop 18-54-00-30-04-2013
===== Slut på eksempel 1 =====
</pre>
    Eksempel 1, som vist ovenfor, starter kl. 18:52:00 d. 30.04 2013. Det åbner firefox i private mode (Det kommer vi ind på senere). Herefter bliver der indtastet et link i adressebaren og der bliver trykket ENTER for at gå ind på linket. Optagelsen slutter så igen kl 18:54:00 d. 30.04 2013. Dette job består af kommandoer der leder browseren hen til det man gerne vil optage, og så ingen kommandoer resten af tiden hvor man bare optager videoen eller et andet stream.
    <br/>
    Grunden til firefox bliver åbnet i private mode er at så er man ikke logget på alle mulige sider som man tidligere har været logget ind på de sidste gange man har optaget noget. På denne måde ved man altid hvordan en side ser ud når man kommer hen til i stedet for at man skal gætte sig frem til om man stadig er logget ind eller ej.
    <br/>
    Et lidt andet og mere udførligt eksempel er som følger:
    <br/>
<pre>
===== Start på eksempel 2 =====
start 13-54-00-10-06-2013
10 open firefox -private
20 paste https://www.facebook.com/pages/Mads-Monopolet-p%C3%A5-P3/83340155905
25 type Return
35 click 1113 196
40 write netlab.netarkivet
45 type Tab
50 write n3t4RK1V
55 type Return
66 click 145 700
70 type Down
71 type Down
72 type Down
73 type Down
74 type Down
75 type Down
76 type Down
77 type Down
78 type Down
80 every 12 type F5
stop 13-56-00-10-06-2013
===== Slut på eksempel 2 =====
</pre>
    <br/>
    Eksempel 2 starter med at optage kl. 13:54:00 d. 10.06 2013. Firefox bliver åbnet i private mode. Så paster vi et link ind (hvorfor kommer vi ind på senere) og trykker på ENTER (Return) for at gå til linket. Vi trykker med musen i loginboksen, skriver vores brugernavn, trykker på Tab for at gå hen til passwordboksen hvor vi skriver vores password. Herefter trykker vi på Enter for at logge ind på facebook. Nu er vi inde på facebooksiden for Mads og Monopolet og vi er logget ind med vores bruger.<br/> Med "click 145 700" klikker vi med musen et vilkårligt sted på siden for at give siden fokus. Dette gør vi således vi kan scrolle ned på siden ved at bruge piletasterne.<br/> Herefter scroller vi nedad på siden med piletasterne, så langt vi nu skal ned for at komme forbi cover-billedet øverst - det er jo ikke interessant indhold.<br/> Og til sidst giver vi kommandoen for at hvert 12. sekund vil vi have trykket på F5 knappen for at genindlæse siden sådan at vi kan følge med om der sker noget udvikling på siden. Jobbet slutter så igen kl. 13:56:00 d. 10.06 2013.
    <br/><br/>
    Hvorfor paster vi nogle gange og writer andre gange?<br/>
    Nogle gange bruger vi paste kommandoen til at indsætte tekst med og andre gange bruger vi write. Hvad er forskellen? write kommandoen skriver teksten bogstav for bogstav ind som vi ville gøre det på tastaturet, men nogle tegn reagerer den lidt underligt på. Som f.eks. det link der er givet i eksempel to hvor "på" bliver lavet om til "p%C3%A3". Så i stedet for at skrive det bogstav for bogstav, beder vi jobbet om at copy paste det i stedet. Forestil dig bare at computeren markerer hele teksten, trykker på CTRL-C og sætter det ind med CTRL-V. Hvis man er usikker på hvilken man skal vælge, anbefaler jeg stærkt at man tester det først.
    <br/>
    Som set ovenfor så kører begge eksempler kun i 2 minutter. Det er nok til at tjekke om de opfører sig ordentligt. Så det kan varmt anbefales, at man tjekker sig job af efter man har lavet det inden man bare sætter det til at køre og tror alt er godt.
    <br/>
    <b>HUSK:</b> Hvis du opretter et nyt job, skal navnet slutte på ".job".</div>
</body></html>