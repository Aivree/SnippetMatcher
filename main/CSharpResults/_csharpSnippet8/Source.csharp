var statuses = from TaskStatus s in Enum.GetValues(typeof(TaskStatus))
               select new { ID = s, Name = s.ToString() };
ViewData["taskStatus"] = new SelectList(statuses, "ID", "Name", task.Status);