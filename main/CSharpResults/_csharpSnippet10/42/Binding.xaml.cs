using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Diagnostics;

namespace FilterControl
{
    /// <summary>
    /// Binding.xaml の相互作用ロジック
    /// </summary>
    public partial class Binding : Window
    {
        public BindingViewModel ViewModel { get { return this.Resources["vm"] as BindingViewModel; } }

        public Binding()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine(ViewModel.Sel);
        }
    }

    public class BindingViewModel
    {
        public Selection Sel { get; set; }
        public int Val { get; set; }
    }

    public enum Selection
    {
        Data1,
        Data2,
    }

    [ValueConversion(typeof(Enum), typeof(bool))]
    public class EnumBoolConvertor : IValueConverter
    {
        // Enum To Bool
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            var pString = parameter as string;
            if (pString == null)
            {
                return DependencyProperty.UnsetValue;
            }

            if (!Enum.IsDefined(value.GetType(), pString))
            {
                return DependencyProperty.UnsetValue;
            }

            var paramValue = Enum.Parse(value.GetType(), pString);

            return value.Equals(paramValue);
        }

        // Bool To Enum
        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            var paramString = parameter as string;
            if (paramString == null) { return DependencyProperty.UnsetValue; }

            var b = (bool)value;
            if (!b) { return DependencyProperty.UnsetValue; }

            return Enum.Parse(targetType, paramString);
        }
    }
}
