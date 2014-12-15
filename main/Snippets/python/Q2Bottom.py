if request.method == 'POST':
    file1 = request.FILES['file']
    contentOfFile = file1.read()
    if file1:
        return render(request, 'blogapp/Statistics.html', {'file': file1, 'contentOfFile': contentOfFile})