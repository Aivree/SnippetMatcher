public class EnumHasFlagToBooleanConverter : IValueConverter
    {
        private object _obj;
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            _obj = value;
            return ((Enum)value).HasFlag((Enum)parameter);
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if (value.Equals(true))
            {
                if (((Enum)_obj).HasFlag((Enum)parameter))
                {
                    // Do nothing
                    return Binding.DoNothing;
                }
                else
                {
                    int i = (int)_obj;
                    int ii = (int)parameter;
                    int newInt = i+ii;
                    return (NavigationProjectDates)newInt;
                }
            }
            else
            {
                if (((Enum)_obj).HasFlag((Enum)parameter))
                {
                    int i = (int)_obj;
                    int ii = (int)parameter;
                    int newInt = i-ii;
                    return (NavigationProjectDates)newInt;

                }
                else
                {
                    // do nothing
                    return Binding.DoNothing;
                }
            }
        }
    }