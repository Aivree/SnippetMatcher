using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace mediatori.Models.etc
{
    [Table("stato")]
    public class Stato
    {
        [Key]
        public int id { get; set; }
        [Required]
        public String descrizione { get; set; }
        [Required]
        public EnumStatoBase statoBase { get; set; }
        [Required]
        public EnumEntitaAssociataStato entitaAssociata { get; set; }
        // per gli stati base di tipo Attivo può essere indicato un gruppo di lavorazione
        public GruppoLavorazione gruppoLavorazione { get; set; }
        public List<Stato> successivi { get; set; }
        public List<Stato> precedenti { get; set; }


    }

    public class StatoSearch {
        public EnumEntitaAssociataStato entita{get;set;}
        public int? codiceStato { get; set; }
        public int? successiviDi { get; set; }
        public int? precedentiDi { get; set; }
    }
    public class StatoView 
    {
        private Stato stato1;
      

        public StatoView(MainDbContext db){
            popolaListeStato(new Stato(),db);
        }

        public StatoView(Stato stato1, MainDbContext db)
        {
            popolaListeStato(stato1, db);
        }


        public void popolaListeStato(Stato stato, MainDbContext db){
            this.stato = stato;
            lstStatoBase = new SelectList(from EnumStatoBase e in EnumStatoBase.GetValues(typeof(EnumStatoBase))
                         select new { Id = e, Name = e.ToString() }, "Id", "Name", stato.statoBase);
            lstEntitaAssociata = new SelectList(from EnumEntitaAssociataStato e in 
                                                     EnumEntitaAssociataStato.GetValues(typeof(EnumEntitaAssociataStato))
                         select new { Id = e, Name = e.ToString() }, "Id", "Name", stato.entitaAssociata);
            if (stato.gruppoLavorazione == null) stato.gruppoLavorazione = new GruppoLavorazione() ;
            lstgruppoLavorazione = new SelectList(from GruppoLavorazione gl in db.gruppiLavorazione 
                                                  select new {Id=gl.id,Name=gl.nome },
                                                  "Id", "Name", stato.gruppoLavorazione.id);
          //    List<String> users = db.Database.SqlQuery<String>("select UserName from dbo.UserProfile").ToList();
          //    gruppoLavorazioneView = GruppoLavorazioneUtils.toView(users, stato.gruppoLavorazione);

        }
        public Stato stato { get; set; }
        public SelectList lstStatoBase { get; set; }
        public SelectList lstEntitaAssociata { get; set; }
        public SelectList lstgruppoLavorazione { get; set; }
    }
}