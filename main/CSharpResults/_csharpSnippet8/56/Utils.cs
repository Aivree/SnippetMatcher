using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Web.Mvc;


namespace VtcIts {

	public static class Utils {

		public static SelectList ListOf<T>() where T : struct, IConvertible {
			var t = typeof (T);
			if (!t.IsEnum) throw new ArgumentException("T must be an enumerated type");

			var values = from Enum e in Enum.GetValues(t)
						 select new {ID = e, Name = e.ToPrintText()};

			return new SelectList(values, "Id", "Name");
		}



		public static SelectList ListOf<T>(object selectedValue) where T : struct, IConvertible {
			var t = typeof (T);
			if (!t.IsEnum) throw new ArgumentException("T must be an enumerated type");

			var values = from Enum e in Enum.GetValues(t)
						 select new {ID = e, Name = e.ToPrintText()};

			return selectedValue == null || !Enum.IsDefined(t, selectedValue)
					   ? new SelectList(values, "Id", "Name")
					   : new SelectList(values, "Id", "Name", selectedValue);
		}



		public static SelectList SortedListOf<T>() where T : struct, IConvertible {
			var t = typeof (T);
			if (!t.IsEnum) throw new ArgumentException("T must be an enumerated type");

			var values = from Enum e in Enum.GetValues(t)
						 select new {ID = e, Ordinal = e.ToOrdinal(), Name = e.ToPrintText()};

			var sortedValues = values.OrderBy(val => val.Ordinal);

			return new SelectList(sortedValues, "Id", "Name");
		}



		public static SelectList ToSelectList(this Enum value) {
			var type = value.GetType();

			var values = from Enum e in Enum.GetValues(type)
						 select new {Id = e, Name = e.ToPrintText()};

			return new SelectList(values, "Id", "Name", value);
		}



		public static SelectList ToSortedSelectList(this Enum value) {
			var type = value.GetType();

			var values = from Enum e in Enum.GetValues(type)
						 select new {ID = e, Ordinal = e.ToOrdinal(), Name = e.ToPrintText()};

			var sortedValues = values.OrderBy(val => val.Ordinal);

			return new SelectList(sortedValues, "Id", "Name", value);
		}



		/// <summary>
		/// Builds a SelectList from a Dictionary
		/// </summary>
		/// <param name="value"></param>
		/// <returns></returns>
		public static SelectList ToSelectList<TKey, TValue>(this Dictionary<TKey, TValue> value) {
			var values = from TKey key in value.Keys
						 select new {Value = Convert.ToString(key), Text = Convert.ToString(value[key])};
			return new SelectList(values, "Value", "Text");
		}



		public static DataSet CallProcedure(string procName, string dataSetName) {
			return CallProcedure(procName, dataSetName, new List<SqlParameter>(), new List<string>());
		}

		public static DataSet CallProcedure(string procName, string dataSetName, IReadOnlyList<string> tableNames) {
			return CallProcedure(procName, dataSetName, new List<SqlParameter>(), tableNames);
		}

		public static DataSet CallProcedure(string procName, string dataSetName, List<SqlParameter> parameters, IReadOnlyList<string> tableNames) {
			var defaultConnection = ConfigurationManager.ConnectionStrings["DefaultConnection"];
			using (var data = new DataSet()) {
				using (var conn = new SqlConnection(defaultConnection.ConnectionString)) {
					using (var adapter = new SqlDataAdapter()) {
						using (var cmd = conn.CreateCommand()) {
							cmd.CommandType = CommandType.StoredProcedure;
							cmd.CommandText = procName;
							cmd.Parameters.AddRange(parameters.ToArray());

							adapter.SelectCommand = cmd;

							conn.Open();
							adapter.Fill(data);
							conn.Close();

							// Clean up results;
							// -----------------------------------------------
							if (!string.IsNullOrEmpty(dataSetName)) {
								data.DataSetName = dataSetName;
							}

							for (var i = 0; i < data.Tables.Count; i++) {
								if (i < tableNames.Count()) {
									data.Tables[i].TableName = tableNames[i];
								}
							}
							return data;
						}
					}
				}
			}


		}

	}

}