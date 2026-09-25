#!/bin/bash

set -e

# GitHub username
GITHUB_USERNAME="alaedd93"

# Chemin vers le fichier contenant uniquement le token GitHub
TOKEN_FILE=".accesskeys/Github PAT.txt"

# Vérifier le message de commit
if [ $# -eq 0 ]; then
    echo "Usage: $0 \"message du commit\""
    exit 1
fi

COMMIT_MESSAGE="$1"

# Vérifier que le fichier du token existe
if [ ! -f "$TOKEN_FILE" ]; then
    echo "Erreur: fichier token introuvable: $TOKEN_FILE"
    exit 1
fi

# Lire le token
GITHUB_TOKEN=$(tr -d '\r\n' < "$TOKEN_FILE")

if [ -z "$GITHUB_TOKEN" ]; then
    echo "Erreur: le fichier token est vide."
    exit 1
fi

echo "Adding files..."
git add .

echo "Creating commit..."
git commit -m "$COMMIT_MESSAGE"

echo "Pushing to GitHub..."

# Script temporaire utilisé par Git pour fournir username + token
ASKPASS_SCRIPT=$(mktemp)

cat > "$ASKPASS_SCRIPT" <<EOF
#!/bin/bash

case "\$1" in
    *Username*)
        echo "$GITHUB_USERNAME"
        ;;
    *Password*)
        echo "$GITHUB_TOKEN"
        ;;
esac
EOF

chmod 700 "$ASKPASS_SCRIPT"

GIT_ASKPASS="$ASKPASS_SCRIPT" \
GIT_TERMINAL_PROMPT=0 \
git push origin HEAD

rm -f "$ASKPASS_SCRIPT"

echo "Done."