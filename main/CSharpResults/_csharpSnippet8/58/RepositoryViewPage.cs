using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using QA_Test_Tracker.Configuration;
using QA_Test_Tracker.Models;
using Siege.Repository;

namespace QA_Test_Tracker.Views
{
    public interface IRepositoryViewPage
    {
        IList<T> GetAll<T>() where T : DomainObject;
    }

    public class RepositoryViewPage<TModel, TDatabase> : ViewPage<TModel>, IRepositoryViewPage where TDatabase : IDatabase
    {
        public IList<T> GetAll<T>() where T : DomainObject
        {
            var repository = StaticServiceLocator.Current.GetInstance<IRepository<TDatabase>>();
            var features = repository.Query<T>().Find();

            return features;
        }

        public SelectList GetAllEnum<TEnum>() 
        {
            var values = from TEnum e in Enum.GetValues(typeof(TEnum))
                         select new { ID = e, Name = e.ToString() };

            return new SelectList(values, "Id", "Name");
        }
    }
}