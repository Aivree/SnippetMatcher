/* LCS Algo
 * finds the longest common subsequence of two strings 
 * of length <=1000
 */
int solve(char a[],char b[]){
    int  n = strlen(a);
    int  m = strlen(b);
    int C[1005][1005];
    for(int i=0;i<n;i++)
        C[i][0]=0;
    for(int i=0;i<m;i++)
        C[0][i]=0;
    for(int i=1;i<=n;i++)
        for(int j=1;j<=m;j++)
            if(a[i-1] == b[j-1])
                C[i][j]=1+C[i-1][j-1];
            else
                C[i][j] = max(C[i][j-1],C[i-1][j]);
    return C[n][m];
}

/* lis() returns the length of the longest increasing subsequence in 
    arr[] of size n */
int lis( int arr[], int n )
{
   int *lis, i, j, max = 0;
   lis = (int*) malloc ( sizeof( int ) * n );
 
   /* Initialize LIS values for all indexes */
   for ( i = 0; i < n; i++ )
      lis[i] = 1;
    
   /* Compute optimized LIS values in bottom up manner */
   for ( i = 1; i < n; i++ )
      for ( j = 0; j < i; j++ )
         if ( arr[i] > arr[j] && lis[i] < lis[j] + 1)
            lis[i] = lis[j] + 1;
    
   /* Pick maximum of all LIS values */
   for ( i = 0; i < n; i++ )
      if ( max < lis[i] )
         max = lis[i];
 
   /* Free memory to avoid memory leak */
   free( lis );
 
   return max;
}


/*********** String split method using any delimiter *********/

vector<string> &split(const string &s, char delim, vector<string> &elems) {
    stringstream ss(s);
    string item;
    while (getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}


vector<string> split(const string &s, char delim) {
    vector<string> elems;
    split(s, delim, elems);
    return elems;
}


//eg
std::vector<std::string> x = split("one:two::three", ':');

/************************************************************/
