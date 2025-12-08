import os
import json
from flask import Flask, render_template, request, redirect, flash, send_from_directory
from groq import Groq
from werkzeug.utils import secure_filename
import base64
from dotenv import load_dotenv

# Charger la clé GROQ_API_KEY du .env
load_dotenv()

app = Flask(__name__)
app.secret_key = 'supersecretkey'
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
def cleanup_uploads():
    for f in os.listdir(UPLOAD_FOLDER):
        try:
            os.remove(os.path.join(UPLOAD_FOLDER, f))
        except:
            pass


client = Groq(api_key=os.environ.get("GROQ_API_KEY"))

def encode_image(image_path):
    with open(image_path, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode('utf-8')

@app.route('/', methods=['GET', 'POST'])
def index():
    extracted = None
    raw_json = None
    error = None
    image_url = None  # URL de l'image à afficher
    if request.method == 'POST':
        cleanup_uploads()
        if 'image' not in request.files:
            flash('Aucun fichier sélectionné')
            return redirect(request.url)
        file = request.files['image']
        if file.filename == '':
            flash('Aucun fichier sélectionné')
            return redirect(request.url)
        filename = secure_filename(file.filename)
        file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(file_path)

        # Générer l'URL de l'image pour l'afficher dans le HTML
        image_url = f"/uploads/{filename}"

        try:
            base64_image = encode_image(file_path)
            completion = client.chat.completions.create(
                model="meta-llama/llama-4-scout-17b-16e-instruct",
                messages=[
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "text",
                                "text": (
                                    "Extract and return all explicit and implicit key information as a JSON object "
                                    "(field:value, or list of objects if needed) from this invoice image. "
                                    "Only output a JSON, no explanations or commentary."
                                )
                            },
                            {
                                "type": "image_url",
                                "image_url": {
                                    "url": f"data:image/jpeg;base64,{base64_image}"
                                }
                            }
                        ]
                    }
                ],
                max_completion_tokens=1024,
                response_format={"type": "json_object"},
            )
            raw_json = completion.choices[0].message.content
            try:
                extracted = json.loads(raw_json)
            except Exception:
                extracted = None
                error = f"Erreur décodage de la réponse JSON : {raw_json[:500]}"
        except Exception as e:
            error = f"Erreur pendant l'extraction : {e}"
       # finally:
            # Assurez-vous que le fichier est fermé avant de le supprimer
           # try:
                #if os.path.exists(file_path):
                    #os.remove(file_path)
           # except PermissionError:
              #  error = f"Impossible de supprimer le fichier : {file_path}. Il est utilisé par un autre processus."
    return render_template('index.html', extracted=extracted, raw_json=raw_json, error=error, image_url=image_url)

@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

if __name__ == '__main__':
    app.run(debug=True)