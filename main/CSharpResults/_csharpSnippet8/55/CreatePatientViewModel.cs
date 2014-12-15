using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web.Mvc;
using OpenEhs.Domain;

namespace OpenEhs.Web.Models
{
    /// <summary>
    /// Create patient view model contains the data and validation required for creating a patient and data sources
    /// for drop down lists
    /// </summary>
    public class CreatePatientViewModel
    {
        [Required(ErrorMessage="Patient's First Name is required.")]
        [Display(Name = "First Name")]
        public string FirstName { get; set; }

        [Display(Name = "Middle Name")]
        public string MiddleName { get; set; }

        [Required(ErrorMessage="Patient's Surname is required.")]
        [Display(Name = "Surname")]
        public string LastName { get; set; }

        [Display(Name = "Place of Birth")]
        public string PlaceOfBirth { get; set; }

        [Required(ErrorMessage="Patient's Date of Birth is required.")]
        [DataType(DataType.Date)]
        [Display(Name = "Date of Birth")]
        [DisplayFormat(DataFormatString = "{0:dd/MM/yyyy}", ApplyFormatInEditMode = true)]
        public DateTime DateOfBirth { get; set; }

        [Display(Name = "Phone Number")]
        public string PhoneNumber { get; set; }

        public MaritalStatus SelectedMaritalStatus { get; set; }

        [Display(Name = "Marital Status")]
        public SelectList MaritalStatus
        {
            get
            {
                var types = from MaritalStatus t in Enum.GetValues(typeof(MaritalStatus))
                            select new { Id = t, Name = t.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        [Required(ErrorMessage="Patient's Street Address is required.")]
        [Display(Name = "Residential Address")]
        public string Street1 { get; set; }

        [Display(Name = "Business Address")]
        public string Street2 { get; set; }

        [Required(ErrorMessage = "Patient's City is required.")]
        public string City { get; set; }

        [Required(ErrorMessage = "Patient's Region is required.")]
        public string Region { get; set; }

        [Required(ErrorMessage = "Patient's Region is required.")]
        [Display(Name = "Region")]
        public SelectList Regions
        {
            get 
            { 
                var regions = new List<object>
                                  {
                                      new {Value = "Eastern", Text = "Eastern"},
                                      new {Value = "Central", Text = "Central"},
                                      new {Value = "Western", Text = "Western"},
                                      new {Value = "Northern", Text = "Northern"},
                                      new {Value = "Ashanti", Text = "Ashanti"},
                                      new {Value = "Volta", Text = "Volta"},
                                      new {Value = "Brong-Ahafo", Text = "Brong-Ahafo"},
                                      new {Value = "Upper East", Text = "Upper East"},
                                      new {Value = "Upper West", Text = "Upper West"},
                                      new {Value = "Greater Accra", Text = "Greater Accra"},
                                      new {Value = "Other", Text = "Other"}
                                  };

                return new SelectList(regions, "Value", "Text", new { Value = "Greater Accra", Text = "Greater Accra" });
            }
        }

        [Required(ErrorMessage = "Patient's Country is required")]
        public Country Country { get; set; }

        [Required(ErrorMessage = "Patient's Country is required")]
        [Display(Name = "Country")]
        public SelectList Countries
        {
            get
            {
                var types = from Country t in Enum.GetValues(typeof(Country))
                            select new { Id = t, Name = t.ToString() };

                return new SelectList(types, "Id", "Name", "Ghana");
            }
        }

        [Display(Name = "Korle Bu ID")]
        public string OldPhysicalRecordNumber { get; set; }

        [Required(ErrorMessage="Emergency Contact's First Name is required")]
        [Display(Name = "First Name")]
        public string EcFirstName { get; set; }

        [Required(ErrorMessage = "Emergency Contact's Last Name is required")]
        [Display(Name = "Last Name")]
        public string EcLastName { get; set; }

        [Display(Name = "Phone Number")]
        public string EcPhoneNumber { get; set; }

        [Display(Name = "Occupation")]
        public string pOccupation { get; set; }

        [Display(Name = "Insurance Number")]
        public string InsuranceNumber { get; set; }

        [DataType(DataType.Date)]
        [DisplayFormat(DataFormatString = "{0:dd/MM/yyyy}", ApplyFormatInEditMode = true)]
        [Display(Name = "Insurance Expiration")]
        public DateTime InsuranceExpiration { get; set; }

        [Required(ErrorMessage = "Emergency Contact's Relationship with the patient is required")]
        [Display(Name = "Relationship")]
        public Relationship EcRelationship { get; set; }

        public SelectList Relationships
        {
            get
            {
                var types = from Relationship s in Enum.GetValues(typeof(Relationship))
                            select new { Id = s, Name = s.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        public Tribes SelectedTribe { get; set; }

        [Display(Name = "Tribe")]
        public SelectList Tribes
        {
            get
            {
                var types = from Tribes t in Enum.GetValues(typeof(Tribes))
                            select new { Id = t, Name = t.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        public Races SelectedRace { get; set; }

        [Display(Name = "Race")]
        public SelectList Races
        {
            get
            {
                var types = from Races r in Enum.GetValues(typeof(Races))
                            select new { Id = r, Name = r.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        public Religions SelectedReligion { get; set; }
        [Display(Name = "Religion")]
        public SelectList Religions
        {
            get
            {
                var types = from Religions y in Enum.GetValues(typeof(Religions))
                            select new { Id = y, Name = y.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        public BloodTypes SelectedBloodType { get; set; }

        public SelectList BloodType
        {
            get
            {
                var types = from BloodTypes o in Enum.GetValues(typeof(BloodTypes))
                            select new { Id = o, Name = o.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        public Education SelectedEducation { get; set; }

        public SelectList Education
        {
            get
            {
                var types = from Education a in Enum.GetValues(typeof(Education))
                            select new { Id = a, Name = a.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

       
        [Required(ErrorMessage = "Patient's Gender is required")]
        public Gender SelectedGender { get; set; }

        [Display(Name = "Gender")]
        public SelectList Gender
        {
            get
            {
                var types = from Gender s in Enum.GetValues(typeof(Gender))
                            select new { Id = s, Name = s.ToString() };

                return new SelectList(types, "Id", "Name");
            }
        }

        [Required(ErrorMessage = "Emergency Contact's street address is required.")]
        [Display(Name = "Residential Address")]
        public string EcStreet1 { get; set; }

        [Required(ErrorMessage = "Emergency Contact's city is required.")]
        [Display(Name = "City")]
        public string EcCity { get; set; }

        [Required(ErrorMessage = "Emergency Contact's region is required.")]
        [Display(Name = "Region")]
        public string EcRegion { get; set; }

        [Required(ErrorMessage = "Emergency Contact's country is required.")]
        [Display(Name = "Country")]
        public Country EcCountry { get; set; }

        /// <summary>
        /// Constructor that initializes the view model with default values
        /// </summary>
        public CreatePatientViewModel()
        {
            //DateOfBirth = DateTime.Now;
            DateOfBirth = new DateTime(1900, 1, 1);
            InsuranceExpiration = DateTime.MinValue;
        }
    }
}