string trim(char const *str)
{
  // Trim leading non-letters
  while(!isalnum(*str)) str++;

  // Trim trailing non-letters
  end = str + strlen(str) - 1;
  while(end > str && !isalnum(*end)) end--;

  return string(str, end+1);
}