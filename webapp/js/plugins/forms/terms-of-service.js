function openFormsTermsOfService(url)
{
  var forms_win_terms_of_service = self.open(url, 'forms_terms_of_service', 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbar=yes,resizable=yes,copyhistory=yes,width=500px,height=300px');
  forms_win_terms_of_service.focus();
}