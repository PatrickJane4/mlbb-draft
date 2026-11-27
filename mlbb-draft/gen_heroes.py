"""Jana app/src/main/assets/heroes.json. DATA CONTOH sahaja, bukan data meta sebenar.
Edit fail ini (atau terus JSON) untuk ganti dengan data kau.
Format hero: id, nama, roles, damage, cc, tank, early, late, wr, strong, weak, build, note
"""
import json, sys

B = lambda *x: list(x)
H = [
 ("chou","Chou",["Fighter"],"physical",3,2,3,2,51.0,["gusion","lesley","hayabusa","eudora","layla"],["khufra","masha","tigreal"],B("Warrior Boots","Bloodlust Axe","Blade of the Heptaseas","Brute Force Breastplate","Immortality"),"Kuat vs hero mudah ditangkap."),
 ("masha","Masha",["Fighter"],"physical",1,2,2,3,50.5,["chou","tigreal","khufra"],["kalea","lunox","eudora"],B("Tough Boots","Bloodlust Axe","Endless Battle","Blade Armor","Immortality"),"Lemah lawan magic burst."),
 ("aulus","Aulus",["Fighter"],"physical",1,2,2,3,51.5,["tigreal","franco","kalea"],["chou","khufra","lancelot"],B("Warrior Boots","Hunter Strike","Blade of Despair","Endless Battle","Immortality"),"Scale lewat, bahaya bila farm."),
 ("saber","Saber",["Assassin"],"physical",2,1,3,2,50.0,["layla","miya","lesley","eudora"],["khufra","tigreal","kalea"],B("Warrior Boots","Hunter Strike","Blade of the Heptaseas","Endless Battle","Rose Gold Meteor"),"Lock target, pick off marksman."),
 ("lancelot","Lancelot",["Assassin"],"physical",1,1,3,2,50.5,["layla","miya","eudora"],["khufra","tigreal","kalea"],B("Warrior Boots","Blade of the Heptaseas","Hunter Strike","Endless Battle","Immortality"),"Mobile, susah ditangkap."),
 ("hayabusa","Hayabusa",["Assassin"],"physical",1,1,2,3,50.5,["layla","miya","eudora","lunox"],["chou","khufra","tigreal"],B("Warrior Boots","Hunter Strike","Blade of the Heptaseas","Endless Battle","Rose Gold Meteor"),"Roam + burst bila ada target."),
 ("natalia","Natalia",["Assassin"],"physical",2,1,2,3,50.0,["layla","miya","lesley","kalea"],["chou","tigreal","khufra"],B("Warrior Boots","Hunter Strike","Blade of Despair","Endless Battle","Rose Gold Meteor"),"Stealth, silence target."),
 ("tigreal","Tigreal",["Tank","Support"],"physical",3,3,2,2,50.0,["layla","miya","lesley","hayabusa","lancelot"],["masha","aulus","kalea"],B("Tough Boots","Dominance Ice","Antique Cuirass","Athena's Shield","Immortality"),"Initiator, ult sedut ramai."),
 ("franco","Franco",["Tank","Support"],"physical",3,3,2,2,50.0,["layla","kalea","lunox","lesley"],["khufra","aulus","chou"],B("Tough Boots","Dominance Ice","Antique Cuirass","Athena's Shield","Immortality"),"Hook = pick off target penting."),
 ("khufra","Khufra",["Tank"],"physical",3,3,2,2,51.0,["hayabusa","lancelot","saber","natalia","gusion","fanny"],["masha","aulus","kaja"],B("Tough Boots","Dominance Ice","Antique Cuirass","Athena's Shield","Immortality"),"Counter hero yang bergantung dash."),
 ("eudora","Eudora",["Mage"],"magic",2,0,3,1,50.5,["layla","tigreal","franco","kaja"],["chou","khufra","lancelot","saber"],B("Arcane Boots","Lightning Truncheon","Holy Crystal","Divine Glaive","Genius Wand"),"Burst tinggi tapi rapuh."),
 ("lunox","Lunox",["Mage"],"mixed",1,1,1,3,51.0,["masha","tigreal","franco","khufra"],["hayabusa","natalia","lancelot","saber"],B("Arcane Boots","Lightning Truncheon","Holy Crystal","Divine Glaive","Blood Wings"),"Late game monster."),
 ("kalea","Kalea",["Fighter","Support"],"magic",3,2,2,2,50.5,["masha","aulus","saber","lancelot","natalia"],["franco","khufra","kaja"],B("Arcane Boots","Concentrated Energy","Dominance Ice","Athena's Shield","Immortality"),"Support fighter dengan CC tebal."),
 ("kaja","Kaja",["Support","Fighter"],"magic",3,2,2,2,50.0,["eudora","layla","hayabusa","gusion"],["chou","aulus","khufra"],B("Tough Boots","Dominance Ice","Athena's Shield","Antique Cuirass","Immortality"),"Pick off target dengan ult."),
 ("lesley","Lesley",["Marksman","Assassin"],"physical",0,0,1,3,50.5,["layla","miya","eudora"],["chou","saber","hayabusa","natalia","franco"],B("Magic Shoes","Wind of Nature","Berserker's Fury","Blade of Despair","Malefic Roar"),"One-shot dari jauh, lemah kalau dah ditangkap."),
 ("miya","Miya",["Marksman"],"physical",0,0,1,3,50.0,["tigreal","franco","khufra"],["lancelot","saber","hayabusa","natalia","eudora"],B("Swift Boots","Scarlet Phantom","Berserker's Fury","Blade of Despair","Immortality"),"Auto-attack, perlukan perlindungan."),
 ("layla","Layla",["Marksman"],"physical",0,0,0,3,49.5,["tigreal","franco","khufra"],["lancelot","saber","hayabusa","natalia","eudora","chou"],B("Swift Boots","Scarlet Phantom","Berserker's Fury","Blade of Despair","Immortality"),"Jarak jauh, rapuh."),
 ("fanny","Fanny",["Assassin"],"physical",1,1,2,3,50.0,["layla","miya","eudora","lunox"],["khufra","tigreal","chou"],B("Warrior Boots","Blade of Despair","Hunter Strike","Endless Battle","Rose Gold Meteor"),"Mekanik tinggi, burst."),
 ("gusion","Gusion",["Assassin","Mage"],"mixed",1,1,3,2,50.5,["layla","miya","eudora","lunox"],["chou","khufra","kaja","tigreal"],B("Arcane Boots","Calamity Reaper","Holy Crystal","Blade of the Heptaseas","Divine Glaive"),"Burst combo."),
 ("hirara","Hirara",["Fighter"],"magic",2,1,2,3,50.5,["layla","miya","lesley","eudora"],["chou","khufra","tigreal"],B("Arcane Boots","Lightning Truncheon","Holy Crystal","Divine Glaive","Blood Wings"),"Skirmisher mobile."),
]

import csv, re
counters=json.load(open('counters.json'))
slug=lambda n: re.sub(r"[^a-z0-9]","",n.lower())
sample={h[0]:h for h in H}
heroes=[]
seen=set()
for row in csv.DictReader(open("stats.csv")):
    i=slug(row["name"]); seen.add(i)
    base=sample.get(i)
    # weak = hero yang counter hero ni (dari kolum COUNTER HERO, dipadankan guna imej)
    d=dict(id=i,name=row["name"],roles=base[2] if base else [],damage=base[3] if base else "unknown",
           cc=base[4] if base else 0,tank=base[5] if base else 0,early=base[6] if base else 0,late=base[7] if base else 0,
           wr=float(row["win"]),pick=float(row["pick"]),ban=float(row["ban"]),
           strong=[],weak=[slug(c) for c in counters.get(row['name'],[])],build=base[11] if base else [],note="")
    heroes.append(d)
for i,b in sample.items():
    if i in seen: continue
    heroes.append(dict(id=i,name=b[1],roles=b[2],damage=b[3],cc=b[4],tank=b[5],early=b[6],late=b[7],
                       wr=50.0,pick=0.0,ban=0.0,strong=[],weak=[],build=b[11],note="Belum ada data ranking"))
json.dump({"_nota":"wr/pick/ban dari mobilelegends.com/rank (screenshot, tahap rank belum dipastikan). Counter dipadankan dari gambar (lihat counters_semak.txt).","heroes":heroes},
          open("app/src/main/assets/heroes.json","w"),ensure_ascii=False,indent=1)
print("OK",len(heroes),"hero")
